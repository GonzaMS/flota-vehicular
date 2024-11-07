package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.car.CarDTO;
import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car.Car;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.services.ICarService;
import com.proyecto.flotavehicular_webapp.services.Redis.RedisServiceImpl;
import com.proyecto.flotavehicular_webapp.utils.EnumUtils;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ICarServiceImpl implements ICarService {

    private final ICarRepository carRepository;
    private final CacheManager cacheManager;
    private final RedisServiceImpl redisServiceImpl;

    private static final String NOTFOUND = "Car not found";

    public ICarServiceImpl(ICarRepository carRepository, CacheManager cacheManager, RedisServiceImpl redisServiceImpl) {
        this.carRepository = carRepository;
        this.cacheManager = cacheManager;
        this.redisServiceImpl = redisServiceImpl;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResponse<CarDTO> getAll(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("brand").ascending());
            Page<Car> carPage = carRepository.findAll(pageable);

            carPage.forEach(car -> {
                String key = RedisUtils.CacheKeyGenerator("sd::api_car_", car.getId());

                Object carOnCache = cacheManager.getCache("sd").get(key);

                if (carOnCache == null) {
                    CarDTO carDTO = mapToDTO(car);
                    redisServiceImpl.save(key, carDTO);
                }
            });
            return mapToPageResponse(carPage);
        } catch (Exception e) {
            log.error("Error getting all cars: {}", e.getMessage());
            throw new ServiceException("Error getting all cars");
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Cacheable(value = "sd", cacheManager = "cacheManagerWithoutTtl", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_car_', #id)")
    public CarDTO getById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(car);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 5)
    @CacheEvict(value = "sd", key = "'api_car__' + #carDTO.state", beforeInvocation = true, allEntries = true)
    public Car save(CarDTO carDTO) {
        try {
            Car car = mapToEntity(carDTO);
            Car savedCar = carRepository.save(car);

            return savedCar;
        } catch (Exception e) {
            log.error("Error al guardar el auto: {}", e.getMessage());
            throw new ServiceException("Error al guardar el auto");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 10)
    @CacheEvict(value = "sd", key = "'api_car_state_' + #carDTO.state", allEntries = true)
    public CarDTO update(Long id, CarDTO carDTO) {
        try {
            Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));

            car.setBrand(carDTO.getBrand());
            car.setModel(carDTO.getModel());
            car.setLicensePlate(carDTO.getLicensePlate());
            car.setFabricationYear(carDTO.getFabricationYear());

            if (!car.getState().equals(carDTO.getState())) {
                cacheManager.getCache("sd").evict("api_car__" + car.getState());
                cacheManager.getCache("sd").evict("api_car__" + carDTO.getState());
            }

            car.setState(carDTO.getState());
            carRepository.save(car);

            return mapToDTO(car);

        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: id={}, carDTO={}", id, carDTO);
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error updating Car: {}, Parameters: id={}, carDTO={}", e.getMessage(), id, carDTO);
            throw new ServiceException("Error updating car");
        }
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = Exception.class, timeout = 10)
    @CacheEvict(value = "sd", cacheManager = "cacheManagerWithoutTtl", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_car_', #id)")
    public void delete(Long id) {
        try {
            Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
            carRepository.delete(car);
        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: id={}", id);
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error deleting Car: {}, Parameters: id={}", e.getMessage(), id);
            throw new ServiceException("Error deleting car");
        }
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = Exception.class, timeout = 10)
    @CacheEvict(value = "sd", key = "'api_car_' + #id", allEntries = true)
    public void deactivate(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        car.setState(ESTATES.INACTIVE);
        carRepository.save(car);

    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = Exception.class, timeout = 10)
    @CacheEvict(value = "sd", key = "'api_car_' + #id", allEntries = true)
    public void activate(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        car.setState(ESTATES.ACTIVE);
        carRepository.save(car);

    }

    // Filters
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "sd", key = "'api_car__' + #state + '_page_' + #pageNumber + '_size_' + #pageSize")
    public PageResponse<CarDTO> getByState(String state, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("brand").ascending());

            if (!EnumUtils.isValidState(state)) {
                throw new NotFoundException("Car state not valid only [ACTIVE, INACTIVE]");
            }

            ESTATES carState = ESTATES.valueOf(state);
            Page<Car> carPage = carRepository.findByState(carState, pageable);

            if (carPage.isEmpty()) {
                throw new NotFoundException("Cars with state " + state + " not found");
            }

            return mapToPageResponse(carPage);
        } catch (NotFoundException e) {
            log.error("Cars with state {} not found", state);
            throw e;
        } catch (Exception e) {
            log.error("Error getting cars by state : {}", e.getMessage());
            throw new ServiceException("Error getting cars by state");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_car_', #brand)")
    public PageResponse<CarDTO> getByBrand(String brand, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Car> carPage = carRepository.findByBrand(brand, pageable);

            if (carPage.isEmpty()) {
                throw new NotFoundException("Cars with brand " + brand + " not found");
            }

            return mapToPageResponse(carPage);
        } catch (NotFoundException e) {
            log.error("Cars with brand {} not found", brand);
            throw e;
        } catch (Exception e) {
            log.error("Error cars by brand: {}", e.getMessage());
            throw new ServiceException("Error getting cars by brand");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_car_', #model)")
    public PageResponse<CarDTO> getByModel(String model, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Car> carPage = carRepository.findByModel(model, pageable);

            if (carPage.isEmpty()) {
                throw new NotFoundException("Cars with model " + model + " not found");
            }

            return mapToPageResponse(carPage);

        } catch (NotFoundException e) {
            log.error("Cars with model {} not found", model);
            throw e;

        } catch (Exception e) {
            log.error("Error getting cars by model: {}", e.getMessage());
            throw new ServiceException("Error getting cars by model");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_car_', #licensePlate)")
    public PageResponse<CarDTO> getByLicensePlate(String licensePlate, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Car> carPage = carRepository.findByLicensePlate(licensePlate, pageable);

            if (carPage.isEmpty()) {
                throw new NotFoundException("Cars with licensePlate " + licensePlate + " not found");
            }

            return mapToPageResponse(carPage);
        } catch (NotFoundException e) {
            log.error("Cars with license plate {} not found", licensePlate);
            throw e;
        } catch (Exception e) {
            log.error("Error getting cars by license plate : {}", e.getMessage());
            throw new ServiceException("Error getting cars by license plate");
        }
    }

    // Mappers
    // Mapping DTO to Car Model
    private Car mapToEntity(CarDTO carDTO) {
        return Car.builder()
                .id(carDTO.getId())
                .brand(carDTO.getBrand())
                .model(carDTO.getModel())
                .licensePlate(carDTO.getLicensePlate())
                .fabricationYear(carDTO.getFabricationYear())
                .state(carDTO.getState())
                .build();
    }

    private CarDTO mapToDTO(Car car) {
        CarDTO.CarDTOBuilder builder = CarDTO.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .licensePlate(car.getLicensePlate())
                .fabricationYear(car.getFabricationYear())
                .state(car.getState());
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
