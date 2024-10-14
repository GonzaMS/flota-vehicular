package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.car.MaintenanceHistoryDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car.Car;
import com.proyecto.flotavehicular_webapp.models.Car.MaintenanceHistory;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.repositories.IMaintenanceRepository;
import com.proyecto.flotavehicular_webapp.services.IMaintenanceService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class IMaintenanceServiceImpl implements IMaintenanceService {

    private final IMaintenanceRepository maintenanceRepository;
    private final ICarRepository carRepository;
    private final CacheManager cacheManager;

    private static final String NOTFOUND = "Maintenance not found";

    public IMaintenanceServiceImpl(IMaintenanceRepository maintenanceRepository, ICarRepository carRepository, CacheManager cacheManager) {
        this.maintenanceRepository = maintenanceRepository;
        this.carRepository = carRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResponse<MaintenanceHistoryDTO> getAll(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<MaintenanceHistory> maintenanceHistoryPage = maintenanceRepository.findAll(pageable);

            maintenanceHistoryPage.forEach(maintenanceHistory -> {
                String key = RedisUtils.CacheKeyGenerator("api_maintenance_", maintenanceHistory.getId());
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
            log.error("Error getting all maintenances: {}", e.getMessage());
            throw new ServiceException("Error getting all maintenances");
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Cacheable(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_maintenance_', #id)")
    public MaintenanceHistoryDTO getById(Long id) {
        MaintenanceHistory maintenanceHistory = maintenanceRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDto(maintenanceHistory);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 10)
    public MaintenanceHistory save(MaintenanceHistoryDTO maintenanceDTO) {
        try {
            Car car = carRepository.findById(maintenanceDTO.getCarId()).orElseThrow(() -> new NotFoundException("Car not found"));

            MaintenanceHistory maintenanceHistory = mapToEntity(maintenanceDTO);
            maintenanceHistory.setCar(car);
            maintenanceHistory.setCreatedAt(new Date());

            return maintenanceRepository.save(maintenanceHistory);

        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: maintenanceDTO={}, carId={}", maintenanceDTO, maintenanceDTO.getId());
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error saving Maintenance: {}, Parameters: maintenanceDTO={}, carId={}", e.getMessage(), maintenanceDTO, maintenanceDTO.getId());
            throw new ServiceException("Error saving maintenance");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 10)
    @CachePut(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_maintenance_', #id)")
    public MaintenanceHistoryDTO update(Long id, MaintenanceHistoryDTO maintenanceDTO) {
        try {
            MaintenanceHistory maintenanceHistory = maintenanceRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));

            maintenanceHistory.setDescription(maintenanceDTO.getDescription());
            maintenanceHistory.setCost(maintenanceDTO.getCost());
            maintenanceHistory.setType(maintenanceDTO.getType());
            maintenanceHistory.setUpdatedAt(new Date());

            maintenanceRepository.save(maintenanceHistory);

            return mapToDto(maintenanceHistory);

        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: id={}, maintenanceDTO={}", id, maintenanceDTO);
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error updating Maintenance: {}, Parameters: id={}, maintenanceDTO={}", e.getMessage(), id, maintenanceDTO);
            throw new ServiceException("Error updating maintenance");
        }
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = Exception.class, timeout = 10)
    @CacheEvict(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_maintenance_', #id)")
    public void delete(Long id) {
        try {
            MaintenanceHistory maintenanceHistory = maintenanceRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
            maintenanceRepository.delete(maintenanceHistory);
        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: id={}", id);
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error deleting Maintenance: {}, Parameters: id={}", e.getMessage(), id);
            throw new ServiceException("Error deleting maintenance");
        }

    }

    //Filters
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, timeout = 5)
    @Cacheable(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_maintenance_car_', #id)")
    public PageResponse<MaintenanceHistoryDTO> getByCarId(Long id, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<MaintenanceHistory> maintenanceHistoryPage = maintenanceRepository.findByCarId(id, pageable);

            if (maintenanceHistoryPage.isEmpty()) {
                throw new NotFoundException(NOTFOUND + " for car with id: " + id);
            }

            return mapToPageResponse(maintenanceHistoryPage);
        } catch (NotFoundException e) {
            log.error("Maintenance not found for car with id: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error getting maintenance by car id: {}", e.getMessage());
            throw new ServiceException("Error getting maintenance by car id");
        }
    }

    // Mappers
    // Map entity to DTO
    private MaintenanceHistoryDTO mapToDto(MaintenanceHistory maintenanceHistory) {
        return MaintenanceHistoryDTO.builder()
                .id(maintenanceHistory.getId())
                .createdAt(maintenanceHistory.getCreatedAt())
                .updatedAt(maintenanceHistory.getUpdatedAt())
                .description(maintenanceHistory.getDescription())
                .cost(maintenanceHistory.getCost())
                .type(maintenanceHistory.getType())
                .carId(maintenanceHistory.getCar().getId())
                .build();
    }

    // Map DTO to entity
    private MaintenanceHistory mapToEntity(MaintenanceHistoryDTO maintenanceDTO) {
        return MaintenanceHistory.builder()
                .id(maintenanceDTO.getId())
                .createdAt(maintenanceDTO.getCreatedAt())
                .updatedAt(maintenanceDTO.getUpdatedAt())
                .description(maintenanceDTO.getDescription())
                .cost(maintenanceDTO.getCost())
                .type(maintenanceDTO.getType())
                .build();
    }

    private PageResponse<MaintenanceHistoryDTO> mapToPageResponse(Page<MaintenanceHistory> maintenanceHistoryPage) {
        List<MaintenanceHistoryDTO> maintenanceDTOList = maintenanceHistoryPage.stream()
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
