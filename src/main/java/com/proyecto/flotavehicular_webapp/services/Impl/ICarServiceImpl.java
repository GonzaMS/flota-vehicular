package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.CarDTO;
import com.proyecto.flotavehicular_webapp.utils.EnumUtils;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.services.ICarService;
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
public class ICarServiceImpl implements ICarService {

    private final ICarRepository carRepository;
    private final CacheManager cacheManager;

    private static final String NOTFOUND = "Car not found";

    private static final Logger logger = LoggerFactory.getLogger(ICarServiceImpl.class);

    public ICarServiceImpl(ICarRepository carRepository, CacheManager cacheManager) {
        this.carRepository = carRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CarDTO> getAll(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Car> carPage = carRepository.findAll(pageable);

            carPage.forEach(car -> {
                String key = RedisUtils.CacheKeyGenerator("api_car_", car.getCarId());
                Cache cache = cacheManager.getCache(key);

                if (cache != null) {
                    Object carOnCache = cache.get(key, Object.class);
                    if (carOnCache == null) {
                        cache.put(key, car);
                    }
                }
            });
            return mapToPageResponse(carPage);
        } catch (Exception e) {
            logger.error("Error getting all cars: {}", e.getMessage());
            throw new ServiceException("Error getting all cars");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_car_', #id)")
    public CarDTO getById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(car);
    }

    @Override
    @Transactional
    public Car save(CarDTO carDTO) {
        try {
            Car car = mapToEntity(carDTO);
            return carRepository.save(car);
        } catch (Exception e) {
            logger.error("Error saving car: {}", e.getMessage());
            throw new ServiceException("Error saving car");
        }
    }

    @Override
    @Transactional
    @CachePut(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_car_', #id)")
    public void update(Long id, CarDTO carDTO) {
        try {
            Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));

            car.setCarBrand(carDTO.getCarBrand());
            car.setCarModel(carDTO.getCarModel());
            car.setCarLicensePlate(carDTO.getCarLicensePlate());
            car.setCarFabricationYear(carDTO.getCarFabricationYear());
            car.setCarState(carDTO.getCarState());

            carRepository.save(car);

        } catch (NotFoundException e) {
            logger.error("Car with id {} not found", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating car: {}", e.getMessage());
            throw new ServiceException("Error updating car");
        }
    }

    @Override
    @Transactional
    @CacheEvict(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_car_', #id)")
    public void delete(Long id) {
        try {
            Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
            carRepository.delete(car);
        } catch (NotFoundException e) {
            logger.error("Car with id {} not found", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting car: {}", e.getMessage());
            throw new ServiceException("Error deleting car");
        }
    }

    @Override
    @Transactional
    @CachePut(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_car_', #id)")
    public void deactivate(Long id) {
        try {
            Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
            car.setCarState(ESTATES.INACTIVE);
            carRepository.save(car);
        } catch (NotFoundException e) {
            logger.error("Car with id {} not found", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error deactivating car: {}", e.getMessage());
            throw new ServiceException("Error deactivating car");
        }
    }

    // Filters
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_car_', #state)")
    public PageResponse<CarDTO> getByState(String state, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            if (!EnumUtils.isValidState(state)) {
                throw new NotFoundException("Car state not valid only [ACTIVE, INACTIVE]");
            }

            ESTATES carState = ESTATES.valueOf(state);

            Page<Car> carPage = carRepository.findByCarState(carState, pageable);

            if (carPage.isEmpty()) {
                throw new NotFoundException("Cars with state " + state + " not found");
            }

            return mapToPageResponse(carPage);
        } catch (NotFoundException e) {
            logger.error("Cars with state {} not found", state);
            throw e;
        } catch (Exception e) {
            logger.error("Error getting cars by state : {}", e.getMessage());
            throw new ServiceException("Error getting cars by state");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_car_', #brand)")
    public PageResponse<CarDTO> getByBrand(String brand, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Car> carPage = carRepository.findByCarBrand(brand, pageable);

            if (carPage.isEmpty()) {
                throw new NotFoundException("Cars with brand " + brand + " not found");
            }

            return mapToPageResponse(carPage);
        } catch (NotFoundException e) {
            logger.error("Cars with brand {} not found", brand);
            throw e;
        } catch (Exception e) {
            logger.error("Error cars by brand: {}", e.getMessage());
            throw new ServiceException("Error getting cars by brand");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_car_', #model)")
    public PageResponse<CarDTO> getByModel(String model, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Car> carPage = carRepository.findByCarModel(model, pageable);

            if (carPage.isEmpty()) {
                throw new NotFoundException("Cars with model " + model + " not found");
            }

            return mapToPageResponse(carPage);

        } catch (NotFoundException e) {
            logger.error("Cars with model {} not found", model);
            throw e;

        } catch (Exception e) {
            logger.error("Error getting cars by model: {}", e.getMessage());
            throw new ServiceException("Error getting cars by model");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_car_', #licensePlate)")
    public PageResponse<CarDTO> getByLicensePlate(String licensePlate, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Car> carPage = carRepository.findByCarLicensePlate(licensePlate, pageable);

            if (carPage.isEmpty()) {
                throw new NotFoundException("Cars with " + licensePlate + " not found");
            }

            return mapToPageResponse(carPage);
        } catch (NotFoundException e) {
            logger.error("Cars with license plate {} not found", licensePlate);
            throw e;
        } catch (Exception e) {
            logger.error("Error getting cars by license plate : {}", e.getMessage());
            throw new ServiceException("Error getting cars by license plate");
        }
    }

    // Mappers
    // Mapping DTO to Car Model
    private Car mapToEntity(CarDTO carDTO) {
        return Car.builder()
                .carId(carDTO.getCarId())
                .carBrand(carDTO.getCarBrand())
                .carModel(carDTO.getCarModel())
                .carLicensePlate(carDTO.getCarLicensePlate())
                .carFabricationYear(carDTO.getCarFabricationYear())
                .carState(carDTO.getCarState())
                .build();
    }

    private CarDTO mapToDTO(Car car) {
        CarDTO.CarDTOBuilder builder = CarDTO.builder()
                .carId(car.getCarId())
                .carBrand(car.getCarBrand())
                .carModel(car.getCarModel())
                .carLicensePlate(car.getCarLicensePlate())
                .carFabricationYear(car.getCarFabricationYear())
                .carState(car.getCarState());
        return builder.build();
    }

    // Page Response
    private PageResponse<CarDTO> mapToPageResponse(Page<Car> carPage) {
        List<CarDTO> carDTOList = carPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                carDTOList,
                carPage.getNumber(),
                carPage.getSize(),
                carPage.getTotalElements(),
                carPage.getTotalPages(),
                carPage.isLast());
    }
}
