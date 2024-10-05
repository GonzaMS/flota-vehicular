package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.DrivingHistoryDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.models.Driver;
import com.proyecto.flotavehicular_webapp.models.DrivingHistory;
import com.proyecto.flotavehicular_webapp.repositories.IDriverRepository;
import com.proyecto.flotavehicular_webapp.repositories.IDrivingHistoryRepository;
import com.proyecto.flotavehicular_webapp.services.ExternalApiService;
import com.proyecto.flotavehicular_webapp.services.IDrivingHistoryService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IDrivingHistoryServiceImpl implements IDrivingHistoryService {

    private final IDrivingHistoryRepository drivingHistoryRepository;

    private static final String NOTFOUND = "Driving history not found";

    private static final Logger logger = LoggerFactory.getLogger(IDrivingHistoryServiceImpl.class);

    private final IDriverRepository driverRepository;
    private final ExternalApiService externalApiService;
    private final CacheManager cacheManager;


    public IDrivingHistoryServiceImpl(IDrivingHistoryRepository drivingHistoryRepository, IDriverRepository driverRepository, ExternalApiService externalApiService, CacheManager cacheManager ) {
        this.drivingHistoryRepository = drivingHistoryRepository;
        this.driverRepository = driverRepository;
        this.externalApiService = externalApiService;
        this.cacheManager = cacheManager;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DrivingHistoryDTO> getAllDrivingHistories(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<DrivingHistory> drivingHistoryPage = drivingHistoryRepository.findAll(pageable);

            drivingHistoryPage.forEach(drivingHistory -> {
                String Key = RedisUtils.CacheKeyGenerator("api_history_", drivingHistory.getDrivingHistoryId());
                Cache cache = cacheManager.getCache(Key);

                if (cache != null) {
                    Object historyOnCache = cache.get(Key, Object.class);
                    if (historyOnCache == null) {
                        cache.put(Key, drivingHistory);
                    }
                }
            });

            return mapToPageResponse(drivingHistoryPage);
        } catch (Exception e) {
            logger.error("Error getting all driving histories: {}", e.getMessage());
            throw new NotFoundException("Error getting all driving histories");
        }
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_history_', #id)")
    public DrivingHistoryDTO getDrivingHistoryById(Long id) {
        DrivingHistory drivingHistory = drivingHistoryRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(drivingHistory);
    }

    @Override
    @Transactional
    public DrivingHistory saveDrivingHistory(DrivingHistoryDTO drivingHistoryDTO) {
        try {
            Driver driver = driverRepository.findById(drivingHistoryDTO.getDriverId()).orElseThrow(() -> new NotFoundException("Driver not found"));
            Car car = externalApiService.callExternalApi(drivingHistoryDTO.getCarId());
            if (car == null){
                throw new NotFoundException("Car not found");
            }

            DrivingHistory drivingHistory = mapToEntity(drivingHistoryDTO);
            drivingHistory.setDriver(driver);
            drivingHistory.setCar(car);

            return drivingHistoryRepository.save(drivingHistory);

        } catch (Exception e) {
            logger.error("Error saving driving history: {}", e.getMessage());
            throw new NotFoundException("Error saving driving history");
        }
    }


    @Override
    @Transactional
    @CachePut(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_history_', #id)")
    public DrivingHistoryDTO updateDrivingHistory(Long id, DrivingHistoryDTO drivingHistoryDTO) {
        try {
            DrivingHistory drivingHistory = drivingHistoryRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));

            drivingHistory.setCreatedAt(drivingHistoryDTO.getCreatedAt());
            drivingHistory.setKmDriven(drivingHistoryDTO.getKmDriven());

            drivingHistoryRepository.save(drivingHistory);

            return mapToDTO(drivingHistory);

        }catch (NotFoundException e) {
            logger.error("History with id {} not found", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating driving history: {}", e.getMessage());
            throw new NotFoundException("Error saving driving history");
        }
    }

    @Override
    @Transactional
    @CacheEvict(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_history_', #id)")
    public void deleteDrivingHistory(Long id) {
        DrivingHistory drivingHistory = drivingHistoryRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        drivingHistoryRepository.delete(drivingHistory);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_history_', #driverId)")
    public PageResponse<DrivingHistoryDTO> getDrivingHistoryByDriverId(Long driverId, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<DrivingHistory> drivingHistoryPage = drivingHistoryRepository.findByDriverDriverId(driverId, pageable);

            if (drivingHistoryPage.isEmpty()) {
                throw new NotFoundException("Driver ID not found: " + driverId);
            }

            return mapToPageResponse(drivingHistoryPage);

        } catch (NotFoundException e) {
            logger.error("Maintenance not found for driver with id: {}", driverId);
            throw e;

        } catch (Exception e) {
            logger.error("Error getting history by driver id: {}", e.getMessage());
            throw new NotFoundException("Error getting history by driver id");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_history_', #carId)")

    public PageResponse<DrivingHistoryDTO> getDrivingHistoryByCarId(Long carId, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<DrivingHistory> drivingHistoryPage = drivingHistoryRepository.findByCar_Id(carId, pageable);

            if (drivingHistoryPage.isEmpty()) {
                throw new NotFoundException("Car ID not found: " + carId);
            }
            return mapToPageResponse(drivingHistoryPage);
        } catch (NotFoundException e) {
            logger.error("Maintenance not found for car with id: {}", carId);
            throw e;

        } catch (Exception e) {
            logger.error("Error getting history by car id: {}", e.getMessage());
            throw new NotFoundException("Error getting history by car id");
        }



    }

    // Mappers
    private DrivingHistoryDTO mapToDTO(DrivingHistory drivingHistory) {
        return DrivingHistoryDTO.builder()
                .drivingHistoryId(drivingHistory.getDrivingHistoryId())
                .createdAt(drivingHistory.getCreatedAt())
                .kmDriven(drivingHistory.getKmDriven())
                .driverId(drivingHistory.getDriver().getDriverId())
                .carId(drivingHistory.getCar().getId())
                .build();
    }

    private DrivingHistory mapToEntity(DrivingHistoryDTO drivingHistoryDTO) {
        return DrivingHistory.builder()
                .drivingHistoryId(drivingHistoryDTO.getDrivingHistoryId())
                .createdAt(drivingHistoryDTO.getCreatedAt())
                .kmDriven(drivingHistoryDTO.getKmDriven())
                .build();
    }

    private PageResponse<DrivingHistoryDTO> mapToPageResponse(Page<DrivingHistory> drivingHistoryPage) {
        List<DrivingHistoryDTO> driverHistoryDTOList = drivingHistoryPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                driverHistoryDTOList,
                drivingHistoryPage.getNumber(),
                drivingHistoryPage.getSize(),
                drivingHistoryPage.getTotalElements(),
                drivingHistoryPage.getTotalPages(),
                drivingHistoryPage.isLast());
    }
}
