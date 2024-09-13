package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.CarDTO;
import com.proyecto.flotavehicular_webapp.dto.CarIncidentsDTO;
import com.proyecto.flotavehicular_webapp.dto.KilometersDTO;
import com.proyecto.flotavehicular_webapp.dto.MaintenanceDTO;
import com.proyecto.flotavehicular_webapp.models.CarIncidents;
import com.proyecto.flotavehicular_webapp.models.Kilometers;
import com.proyecto.flotavehicular_webapp.models.MaintenanceHistory;
import com.proyecto.flotavehicular_webapp.utils.EnumUtils;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.services.ICarService;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
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
    public PageResponse<CarDTO> getAll(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Car> carPage = carRepository.findAll(pageable);

            return mapToPageResponse(carPage, false);
        } catch (Exception e) {
            logger.error("Error getting all cars: {}", e.getMessage());
            throw new ServiceException("Error getting all cars");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cars", key = "'api_cars_'+ #id")
    public CarDTO getById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(car, true);
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

            return mapToPageResponse(carPage, false);
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
    public PageResponse<CarDTO> getByBrand(String brand, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Car> carPage = carRepository.findByCarBrand(brand, pageable);

            if (carPage.isEmpty()) {
                throw new NotFoundException("Cars with brand " + brand + " not found");
            }

            return mapToPageResponse(carPage, false);
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
    public PageResponse<CarDTO> getByModel(String model, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Car> carPage = carRepository.findByCarModel(model, pageable);

            if (carPage.isEmpty()) {
                throw new NotFoundException("Cars with model " + model + " not found");
            }

            return mapToPageResponse(carPage, false);

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
    public PageResponse<CarDTO> getByLicensePlate(String licensePlate, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Car> carPage = carRepository.findByCarLicensePlate(licensePlate, pageable);

            if (carPage.isEmpty()) {
                throw new NotFoundException("Cars with " + licensePlate + " not found");
            }

            return mapToPageResponse(carPage, false);
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
                .maintenanceHistories(carDTO.getMaintenanceHistories() != null ?
                        carDTO.getMaintenanceHistories().stream()
                                .map(this::mapToMaintenanceEntity) // Map to MaintenanceHistory entity
                                .toList()
                        : Collections.emptyList())
                .carIncidents(carDTO.getCarIncidents() != null ?
                        carDTO.getCarIncidents().stream()
                                .map(this::mapToCarIncidentsEntity) // Map to CarIncidents entity
                                .toList()
                        : Collections.emptyList())
                .carKilometers(carDTO.getCarKilometers() != null ?
                        carDTO.getCarKilometers().stream()
                                .map(this::mapToKilometersEntity) // Map to Kilometers entity
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

    // Mapping CarIncidentsDTO to CarIncidents entity
    private CarIncidents mapToCarIncidentsEntity(CarIncidentsDTO carIncidentsDTO) {
        return CarIncidents.builder()
                .incidentId(carIncidentsDTO.getIncidentId())
                .car(Car.builder().carId(carIncidentsDTO.getCarId()).build())
                .incidentDate(carIncidentsDTO.getIncidentDate())
                .incidentDescription(carIncidentsDTO.getIncidentDescription())
                .incidentType(carIncidentsDTO.getIncidentType())
                .build();
    }

    // Mapping KilometersDTO to Kilometers entity
    private Kilometers mapToKilometersEntity(KilometersDTO kilometersDTO) {
        return Kilometers.builder()
                .kilometersId(kilometersDTO.getKilometersId())
                .car(Car.builder().carId(kilometersDTO.getCarId()).build())
                .updateKmDate(kilometersDTO.getUpdateKmDate())
                .actualKm(kilometersDTO.getActualKm())
                .build();
    }

    // Mapping the Car object to DTO
    private CarDTO mapToDTO(Car car, boolean includeRelations) {
        CarDTO.CarDTOBuilder builder = CarDTO.builder()
                .carId(car.getCarId())
                .carBrand(car.getCarBrand())
                .carModel(car.getCarModel())
                .carLicensePlate(car.getCarLicensePlate())
                .carFabricationYear(car.getCarFabricationYear())
                .carState(car.getCarState());

        if (includeRelations) {
            builder.maintenanceHistories(car.getMaintenanceHistories() != null ?
                            car.getMaintenanceHistories().stream().map(this::mapToMaintenanceDTO).toList() : Collections.emptyList())
                    .carIncidents(car.getCarIncidents() != null ?
                            car.getCarIncidents().stream().map(this::mapToCarIncidentsDTO).toList() : Collections.emptyList())
                    .carKilometers(car.getCarKilometers() != null ?
                            car.getCarKilometers().stream().map(this::mapToKilometersDTO).toList() : Collections.emptyList());
        }
        return builder.build();
    }

    // Mapping CarIncidents dto to Entity
    private CarIncidentsDTO mapToCarIncidentsDTO(CarIncidents carIncidents) {
        return CarIncidentsDTO.builder()
                .incidentId(carIncidents.getIncidentId())
                .carId(carIncidents.getCar().getCarId())
                .incidentDate(carIncidents.getIncidentDate())
                .incidentDescription(carIncidents.getIncidentDescription())
                .incidentType(carIncidents.getIncidentType())
                .build();
    }

    // Mapping MaintenanceHistory dto to Entity
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

    // Mapping Kilometers dto to Entity
    private KilometersDTO mapToKilometersDTO(Kilometers kilometersDTO) {
        return KilometersDTO.builder()
                .kilometersId(kilometersDTO.getKilometersId())
                .carId(kilometersDTO.getCar().getCarId())
                .updateKmDate(kilometersDTO.getUpdateKmDate())
                .actualKm(kilometersDTO.getActualKm())
                .build();
    }

    // Page Response
    private PageResponse<CarDTO> mapToPageResponse(Page<Car> carPage, Boolean includeRelations) {
        List<CarDTO> carDTOList = carPage.stream()
                .map(car -> mapToDTO(car, includeRelations))
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
