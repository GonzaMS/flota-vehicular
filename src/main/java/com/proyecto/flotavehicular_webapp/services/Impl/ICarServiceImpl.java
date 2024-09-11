package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.CarDTO;
import com.proyecto.flotavehicular_webapp.dto.MaintenanceDTO;
import com.proyecto.flotavehicular_webapp.models.MaintenanceHistory;
import com.proyecto.flotavehicular_webapp.utils.EnumUtils;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.services.ICarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class ICarServiceImpl implements ICarService {

    private final ICarRepository carRepository;

    private static final String NOTFOUND = "Car not found";

    private static final Logger logger = LoggerFactory.getLogger(ICarServiceImpl.class);


    public ICarServiceImpl(ICarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CarDTO> getAllCars(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Car> carPage = carRepository.findAll(pageable);

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
        } catch (Exception e) {
            logger.error("Error getting all cars: {}", e.getMessage());
            throw new NotFoundException("Error getting all cars");
        }

    }

    @Override
    @Transactional(readOnly = true)
    public CarDTO getCarById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(car);
    }

    @Override
    @Transactional
    public Car saveCar(CarDTO carDTO) {
        try {
            Car car = mapToEntity(carDTO);
            return carRepository.save(car);
        } catch (Exception e) {
            logger.error("Error saving car: {}", e.getMessage());
            throw new NotFoundException("Error saving car");
        }

    }

    @Override
    @Transactional
    public void updateCar(Long id, CarDTO carDTO) {
        try {
            Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));

            car.setCarBrand(carDTO.getCarBrand());
            car.setCarModel(carDTO.getCarModel());
            car.setCarLicensePlate(carDTO.getCarLicensePlate());
            car.setCarFabricationYear(carDTO.getCarFabricationYear());
            car.setCarState(carDTO.getCarState());

            carRepository.save(car);

        } catch (Exception e) {
            logger.error("Error updating car: {}", e.getMessage());
            throw new NotFoundException("Error updating car");
        }
    }

    @Override
    @Transactional
    public void deleteCar(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        carRepository.delete(car);
    }

    @Override
    @Transactional
    public void deactivateCar(Long id) {
        try {
            Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
            car.setCarState(ESTATES.INACTIVE);
            carRepository.save(car);
        } catch (Exception e) {
            logger.error("Error deactivating car: {}", e.getMessage());
            throw new NotFoundException("Error deactivating car");
        }
    }

    // Filters
    @Override
    @Transactional(readOnly = true)
    public PageResponse<CarDTO> getCarByState(String state, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            if (!EnumUtils.isValidState(state)) {
                throw new NotFoundException("State not found " + state);
            }

            ESTATES carState = ESTATES.valueOf(state);

            Page<Car> carPage = carRepository.findByCarState(carState, pageable);

            List<CarDTO> carDTOList = carPage.stream()
                    .map(this::mapToDTO)
                    .toList();

            return PageResponse.of(
                    carDTOList,
                    carPage.getNumber(),
                    carPage.getSize(),
                    carPage.getTotalElements(),
                    carPage.getTotalPages(),
                    carPage.isLast()
            );
        } catch (Exception e) {
            logger.error("Error getting all incidents: {}", e.getMessage());
            throw new NotFoundException("Error getting all incidents");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CarDTO> getCarByBrand(String brand, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Car> carPage = carRepository.findByCarBrand(brand, pageable);

            if (carPage.isEmpty()) {
                throw new NotFoundException("Brand not found " + brand);
            }

            List<CarDTO> carDTOList = carPage.stream()
                    .map(this::mapToDTO)
                    .toList();

            return PageResponse.of(
                    carDTOList,
                    carPage.getNumber(),
                    carPage.getSize(),
                    carPage.getTotalElements(),
                    carPage.getTotalPages(),
                    carPage.isLast()
            );
        } catch (Exception e) {
            logger.error("Error getting all incidents: {}", e.getMessage());
            throw new NotFoundException("Error getting all incidents");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CarDTO> getCarByModel(String model, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Car> carPage = carRepository.findByCarModel(model, pageable);

            if (carPage.isEmpty()) {
                throw new NotFoundException("Model not found " + model);
            }

            List<CarDTO> carDTOList = carPage.stream()
                    .map(this::mapToDTO)
                    .toList();

            return PageResponse.of(
                    carDTOList,
                    carPage.getNumber(),
                    carPage.getSize(),
                    carPage.getTotalElements(),
                    carPage.getTotalPages(),
                    carPage.isLast()
            );
        } catch (Exception e) {
            logger.error("Error getting all incidents: {}", e.getMessage());
            throw new NotFoundException("Error getting all incidents");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CarDTO> getCarByLicensePlate(String licensePlate, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Car> carPage = carRepository.findByCarLicensePlate(licensePlate, pageable);

            if (carPage.isEmpty()) {
                throw new NotFoundException("License Plate not found " + licensePlate);
            }

            List<CarDTO> carDTOList = carPage.stream()
                    .map(this::mapToDTO)
                    .toList();

            return PageResponse.of(
                    carDTOList,
                    carPage.getNumber(),
                    carPage.getSize(),
                    carPage.getTotalElements(),
                    carPage.getTotalPages(),
                    carPage.isLast()
            );
        } catch (Exception e) {
            logger.error("Error getting all incidents: {}", e.getMessage());
            throw new NotFoundException("Error getting all incidents");
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
                .maintenanceHistories(carDTO.getMaintenanceHistories() != null ?
                        carDTO.getMaintenanceHistories().stream()
                                .map(this::mapToMaintenanceEntity) // Map to MaintenanceHistory entity
                                .toList()
                        : Collections.emptyList())
                .build();
    }

    // Mapping MaintenanceDTO to MaintenanceHistory entity
    private MaintenanceHistory mapToMaintenanceEntity(MaintenanceDTO maintenanceDTO) {
        return MaintenanceHistory.builder()
                .maintenanceId(maintenanceDTO.getMaintenanceId())
                .car(Car.builder().carId(maintenanceDTO.getCarId()).build())
                .maintenanceDate(maintenanceDTO.getMaintenanceDate())
                .maintenanceDescription(maintenanceDTO.getMaintenanceDescription())
                .maintenanceCost(maintenanceDTO.getMaintenanceCost())
                .maintenanceType(maintenanceDTO.getMaintenanceType())
                .build();
    }

    // Mapping the Car object to DTO
    private CarDTO mapToDTO(Car car) {
        return CarDTO.builder()
                .carId(car.getCarId())
                .carBrand(car.getCarBrand())
                .carModel(car.getCarModel())
                .carLicensePlate(car.getCarLicensePlate())
                .carState(car.getCarState())
                .carFabricationYear(car.getCarFabricationYear())
                .maintenanceHistories(car.getMaintenanceHistories() != null ?
                        car.getMaintenanceHistories().stream()
                                .map(this::mapToMaintenanceDTO) // Map to MaintenanceDTO
                                .toList()
                        : Collections.emptyList())
                .build();
    }

    // Mapping MaintenanceHistory entity to MaintenanceDTO
    private MaintenanceDTO mapToMaintenanceDTO(MaintenanceHistory maintenanceHistory) {
        return MaintenanceDTO.builder()
                .maintenanceId(maintenanceHistory.getMaintenanceId())
                .carId(maintenanceHistory.getCar().getCarId())
                .maintenanceDate(maintenanceHistory.getMaintenanceDate())
                .maintenanceDescription(maintenanceHistory.getMaintenanceDescription())
                .maintenanceCost(maintenanceHistory.getMaintenanceCost())
                .maintenanceType(maintenanceHistory.getMaintenanceType())
                .build();
    }
}
