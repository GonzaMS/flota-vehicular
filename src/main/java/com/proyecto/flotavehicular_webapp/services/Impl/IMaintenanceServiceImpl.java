package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.MaintenanceDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.models.MaintenanceHistory;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.repositories.IMaintenanceRepository;
import com.proyecto.flotavehicular_webapp.services.IMaintenanceService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.utils.RedisUtils;
import org.hibernate.service.spi.ServiceException;
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
public class IMaintenanceServiceImpl implements IMaintenanceService {

    private final IMaintenanceRepository maintenanceRepository;
    private final ICarRepository carRepository;
    private final CacheManager cacheManager;

    private static final String NOTFOUND = "Maintenance not found";

    private static final Logger logger = LoggerFactory.getLogger(IMaintenanceServiceImpl.class);

    public IMaintenanceServiceImpl(IMaintenanceRepository maintenanceRepository, ICarRepository carRepository, CacheManager cacheManager) {
        this.maintenanceRepository = maintenanceRepository;
        this.carRepository = carRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MaintenanceDTO> getAll(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<MaintenanceHistory> maintenanceHistoryPage = maintenanceRepository.findAll(pageable);

            maintenanceHistoryPage.forEach(maintenanceHistory -> {
                String key = RedisUtils.CacheKeyGenerator("api_maintenance_", maintenanceHistory.getMaintenanceId());
                Cache cache = cacheManager.getCache(key);

                if (cache != null) {
                    Object maintenanceOnCache = cache.get(key, Object.class);
                    if (maintenanceOnCache == null) {
                        cache.put(key, maintenanceHistory);
                    }
                }
            });

            return mapToPageResponse(maintenanceHistoryPage);
        } catch (Exception e) {
            logger.error("Error getting all maintenances: {}", e.getMessage());
            throw new ServiceException("Error getting all maintenances");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_maintenance_', #id)")
    public MaintenanceDTO getById(Long id) {
        MaintenanceHistory maintenanceHistory = maintenanceRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDto(maintenanceHistory);
    }

    @Override
    @Transactional
    public MaintenanceHistory save(MaintenanceDTO maintenanceDTO) {
        try {
            Car car = carRepository.findById(maintenanceDTO.getCarId()).orElseThrow(() -> new NotFoundException("Car not found"));

            MaintenanceHistory maintenanceHistory = mapToEntity(maintenanceDTO);
            maintenanceHistory.setCar(car);

            return maintenanceRepository.save(maintenanceHistory);

        } catch (NotFoundException e) {
            logger.error("Car with id {} not found", maintenanceDTO.getCarId());
            throw e;
        } catch (Exception e) {
            logger.error("Error saving maintenance: {}", e.getMessage());
            throw new ServiceException("Error saving maintenance");
        }
    }

    @Override
    @Transactional
    @CachePut(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_maintenance_', #id)")
    public MaintenanceDTO update(Long id, MaintenanceDTO maintenanceDTO) {
        try {
            MaintenanceHistory maintenanceHistory = maintenanceRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));

            maintenanceHistory.setMaintenanceDate(maintenanceDTO.getMaintenanceDate());
            maintenanceHistory.setMaintenanceDescription(maintenanceDTO.getMaintenanceDescription());
            maintenanceHistory.setMaintenanceCost(maintenanceDTO.getMaintenanceCost());
            maintenanceHistory.setMaintenanceType(maintenanceDTO.getMaintenanceType());

            maintenanceRepository.save(maintenanceHistory);

            return mapToDto(maintenanceHistory);

        } catch (NotFoundException e) {
            logger.error("Maintenance with id {} not found", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating maintenance: {}", e.getMessage());
            throw new ServiceException("Error updating maintenance");
        }
    }

    @Override
    @Transactional
    @CacheEvict(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_maintenance_', #id)")
    public void delete(Long id) {
        MaintenanceHistory maintenanceHistory = maintenanceRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        maintenanceRepository.delete(maintenanceHistory);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_maintenance_car_', #id)")
    public PageResponse<MaintenanceDTO> getByCarId(Long id, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<MaintenanceHistory> maintenanceHistoryPage = maintenanceRepository.findByCar_CarId(id, pageable);

            if (maintenanceHistoryPage.isEmpty()) {
                throw new NotFoundException(NOTFOUND + " for car with id: " + id);
            }

            return mapToPageResponse(maintenanceHistoryPage);
        } catch (NotFoundException e) {
            logger.error("Maintenance not found for car with id: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error getting maintenance by car id: {}", e.getMessage());
            throw new ServiceException("Error getting maintenance by car id");
        }
    }

    // Mappers
    // Map entity to DTO
    private MaintenanceDTO mapToDto(MaintenanceHistory maintenanceHistory) {
        return MaintenanceDTO.builder()
                .maintenanceId(maintenanceHistory.getMaintenanceId())
                .maintenanceDate(maintenanceHistory.getMaintenanceDate())
                .maintenanceDescription(maintenanceHistory.getMaintenanceDescription())
                .maintenanceCost(maintenanceHistory.getMaintenanceCost())
                .maintenanceType(maintenanceHistory.getMaintenanceType())
                .carId(maintenanceHistory.getCar().getCarId())
                .build();
    }

    // Map DTO to entity
    private MaintenanceHistory mapToEntity(MaintenanceDTO maintenanceDTO) {
        return MaintenanceHistory.builder()
                .maintenanceId(maintenanceDTO.getMaintenanceId())
                .maintenanceDate(maintenanceDTO.getMaintenanceDate())
                .maintenanceDescription(maintenanceDTO.getMaintenanceDescription())
                .maintenanceCost(maintenanceDTO.getMaintenanceCost())
                .maintenanceType(maintenanceDTO.getMaintenanceType())
                .build();
    }

    private PageResponse<MaintenanceDTO> mapToPageResponse(Page<MaintenanceHistory> maintenanceHistoryPage) {
        List<MaintenanceDTO> maintenanceDTOList = maintenanceHistoryPage.stream()
                .map(this::mapToDto)
                .toList();

        return PageResponse.of(
                maintenanceDTOList,
                maintenanceHistoryPage.getNumber(),
                maintenanceHistoryPage.getSize(),
                maintenanceHistoryPage.getTotalElements(),
                maintenanceHistoryPage.getTotalPages(),
                maintenanceHistoryPage.isLast()
        );
    }
}
