package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.driver.DriverDTO;
import com.proyecto.flotavehicular_webapp.services.Redis.RedisServiceImpl;
import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Driver.Driver;
import com.proyecto.flotavehicular_webapp.repositories.IDriverRepository;
import com.proyecto.flotavehicular_webapp.services.IDriverService;
import com.proyecto.flotavehicular_webapp.utils.EnumUtils;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.proyecto.flotavehicular_webapp.utils.RedisUtils;



import java.util.List;

@Service
public class IDriverServiceImpl implements IDriverService {

    private final IDriverRepository driverRepository;
    private final CacheManager cacheManager;
    private final RedisServiceImpl redisServiceImpl;

    private static final String NOTFOUND = "Driver not found";

    private static final Logger logger = LoggerFactory.getLogger(IDriverServiceImpl.class);


    public IDriverServiceImpl(IDriverRepository driverRepository, CacheManager cacheManager, RedisServiceImpl redisServiceImpl) {
        this.driverRepository = driverRepository;
        this.cacheManager = cacheManager;
        this.redisServiceImpl = redisServiceImpl;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DriverDTO> getAllDrivers(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Driver> driverPage = driverRepository.findAll(pageable);

            driverPage.forEach(driver -> {
                String key = RedisUtils.CacheKeyGenerator("sd::api_driver_", driver.getDriverId());

                Object driverOnCache = cacheManager.getCache("sd").get(key);

                if (driverOnCache == null){
                    DriverDTO driverDTO = mapToDTO(driver);
                    redisServiceImpl.save(key, driverDTO);
                }
            });

            return mapToPageResponse(driverPage);
        }catch (Exception e) {
                logger.error("Error getting all cars: {}", e.getMessage());
                throw new NotFoundException("Error getting all cars");
            }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "sd", cacheManager = "cacheManagerWithoutTtl", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_driver_', #id)")
    public DriverDTO getDriverById(Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(driver);
    }

    @Override
    @Transactional
    public Driver saveDriver(DriverDTO driverDTO) {
        try {
            Driver driver = mapToEntity(driverDTO);
            return driverRepository.save(driver);
        } catch (Exception e) {
            logger.error("Error saving driver: {}", e.getMessage());
            throw new ServiceException("Error saving car");
        }
    }

    @Override
    @Transactional
    @CachePut(value = "sd", cacheManager = "cacheManagerWithoutTtl", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_driver_', #id)", unless = "#result == null")
    public DriverDTO updateDriver(Long id, DriverDTO driverDTO) {
        try{
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));

        driver.setDriverName(driverDTO.getDriverName());
        driver.setDriverLicense(driverDTO.getDriverLicense());
        driver.setDriverLicenseExpirationDate(driverDTO.getDriverLicenseExpirationDate());
        driver.setDriverState(driverDTO.getDriverState());

        driverRepository.save(driver);

        return mapToDTO(driver);

    } catch (NotFoundException e) {
        logger.error("driver with id {} not found", id);
        throw e;
    }catch (Exception e) {
            logger.error("Error updating driver: {}", e.getMessage());
        throw new ServiceException("Error updating driver");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "sd", cacheManager = "cacheManagerWithoutTtl", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_driver_', #id)")
    public void deleteDriver(Long id) {
        try {
            Driver driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
            driverRepository.delete(driver);
        }catch (NotFoundException e) {
                logger.error("Car with id {} not found", id);
                throw e;
        } catch (Exception e) {
            logger.error("Error deleting car: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Filters
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_driver_', #name)")
    public PageResponse<DriverDTO> getDriverByName(String name, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Driver> driverPage = driverRepository.findByDriverName(name, pageable);

            if (driverPage.isEmpty()) {
                throw new NotFoundException("Name not found: " + name);
            }

            return mapToPageResponse(driverPage);

        }catch (NotFoundException e) {
            logger.error("driver with name {} not found", name);
            throw e;

        } catch (Exception e) {
            logger.error("Error getting driver by name: {}", e.getMessage());
            throw new ServiceException("Error getting driver by name");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_driver_', #license)")
    public PageResponse<DriverDTO> getDriverByLicense(String license, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Driver> driverPage = driverRepository.findByDriverLicense(license, pageable);

            if (driverPage.isEmpty()) {
                throw new NotFoundException("License not found: " + license);
            }

            return mapToPageResponse(driverPage);
        } catch (NotFoundException e) {
            logger.error("driver with license {} not found", license);
            throw e;

        } catch (Exception e) {
            logger.error("Error getting driver by name: {}", e.getMessage());
            throw new ServiceException("Error getting driver by license");
        }

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_driver_', #state)")
    public PageResponse<DriverDTO> getDriverByState(String state, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            if(!EnumUtils.isValidState(state)) {
                throw new NotFoundException("State not found: " + state);
            }
            ESTATES driverState = ESTATES.valueOf(state);
            Page<Driver> driverPage = driverRepository.findByDriverState(driverState, pageable);

            if (driverPage.isEmpty()) {
                throw new NotFoundException("State not found: " + state);
            }

            return mapToPageResponse(driverPage);
        } catch (NotFoundException e) {
            logger.error("driver with state {} not found", state);
            throw e;

        } catch (Exception e) {
            logger.error("Error getting driver by state: {}", e.getMessage());
            throw new ServiceException("Error getting driver by state");
        }
    }

    // Mappers
    private Driver mapToEntity(DriverDTO driverDTO) {
        return Driver.builder()
                .driverId(driverDTO.getDriverId())
                .driverName(driverDTO.getDriverName())
                .driverLicense(driverDTO.getDriverLicense())
                .driverState(driverDTO.getDriverState())
                .driverLicenseExpirationDate(driverDTO.getDriverLicenseExpirationDate())
                .userId(driverDTO.getUserId())
                .build();
    }

    // Mapping the Driver object to DTO
    private DriverDTO mapToDTO(Driver driver) {
        DriverDTO.DriverDTOBuilder builder = DriverDTO.builder()
                .driverId(driver.getDriverId())
                .driverLicense(driver.getDriverLicense())
                .driverName(driver.getDriverName())
                .driverState(driver.getDriverState())
                .driverLicenseExpirationDate(driver.getDriverLicenseExpirationDate())
                .userId(driver.getUserId());
        return builder.build();
    }

    // Page Response
    private PageResponse<DriverDTO> mapToPageResponse(Page<Driver> carPage ) {
        List<DriverDTO> driverDTOList = carPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                driverDTOList,
                carPage.getNumber(),
                carPage.getSize(),
                carPage.getTotalElements(),
                carPage.getTotalPages(),
                carPage.isLast());
    }
}