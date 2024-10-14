package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.car.*;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car.Car;
import com.proyecto.flotavehicular_webapp.models.Car.CarIncidents;
import com.proyecto.flotavehicular_webapp.models.Car.Kilometers;
import com.proyecto.flotavehicular_webapp.models.Car.MaintenanceHistory;
import com.proyecto.flotavehicular_webapp.repositories.ICarIncidentsRepository;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.repositories.IKilometersRepository;
import com.proyecto.flotavehicular_webapp.repositories.IMaintenanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class IHeaderDetailsServiceImpl {

    private final ICarRepository carRepository;
    private final ICarIncidentsRepository carIncidentsRepository;
    private final IKilometersRepository kilometersRepository;
    private final IMaintenanceRepository maintenanceRepository;

    private static final String NOTFOUND = "Car not found";

    public IHeaderDetailsServiceImpl(ICarRepository carRepository, ICarIncidentsRepository carIncidentsRepository,
                                     IKilometersRepository kilometersRepository, IMaintenanceRepository maintenanceRepository) {
        this.carRepository = carRepository;
        this.carIncidentsRepository = carIncidentsRepository;
        this.kilometersRepository = kilometersRepository;
        this.maintenanceRepository = maintenanceRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @CachePut(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('car_with_details_', #id)")
    public CarWithDetailsDTO getCarWithDetails(Long id, int pageNumber, int pageSize) {
        try {
            Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));

            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<CarIncidents> incidentsPage = carIncidentsRepository.findByCarId(id, pageable);
            Page<Kilometers> kilometersPage = kilometersRepository.findByCarId(id, pageable);
            Page<MaintenanceHistory> maintenancePage = maintenanceRepository.findByCarId(id, pageable);

            CarDTO carDTO = mapToCarDTO(car);
            List<CarIncidentsDTO> incidentDTOs = incidentsPage.stream().map(this::mapToCarIncidentsDTO).toList();
            List<KilometersDTO> kilometersDTOs = kilometersPage.stream().map(this::mapToKilometersDTO).toList();
            List<MaintenanceHistoryDTO> maintenanceDTOs = maintenancePage.stream().map(this::mapToMaintenanceDTO).toList();

            return CarWithDetailsDTO.builder()
                    .incidents(incidentDTOs)
                    .kilometers(kilometersDTOs)
                    .maintenance(maintenanceDTOs)
                    .build();
        } catch (Exception e) {
            log.error("Error getting car with details: {}", e.getMessage());
            throw new ServiceException("Error updating car");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 10)
    @CachePut(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('car_with_details_', #carId)")
    public void updateIncidents(Long carId, List<CarIncidentsDTO> incidentsDTOs) {
        try {
            List<CarIncidents> incidents = incidentsDTOs.stream().map(this::mapToCarIncidentsEntity).toList();
            carIncidentsRepository.saveAll(incidents);

        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: carId={}, incidentsDTOs={}", carId, incidentsDTOs);
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error updating incident: {}, Parameters: carId={}, incidentsDTOs={}", e.getMessage(), carId, incidentsDTOs);
            throw new ServiceException("Error updating car");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 10)
    @CachePut(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('car_with_details_', #carId)")
    public void updateKilometers(Long carId, List<KilometersDTO> kilometersDTOs) {
        try {
            List<Kilometers> kilometers = kilometersDTOs.stream().map(this::mapToKilometersEntity).toList();
            kilometersRepository.saveAll(kilometers);

        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: carId={}, kilometersDTOs={}", carId, kilometersDTOs);
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error updating kilometers: {}, Parameters: carId={}, kilometersDTOs={}", e.getMessage(), carId, kilometersDTOs);
            throw new ServiceException("Error updating car");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 10)
    @CachePut(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('car_with_details_', #carId)")
    public void updateMaintenance(Long carId, List<MaintenanceHistoryDTO> maintenanceDTOs) {
        try {
            List<MaintenanceHistory> maintenance = maintenanceDTOs.stream().map(this::mapToMaintenanceEntity).toList();
            maintenanceRepository.saveAll(maintenance);

        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: carId={}, maintenanceDTOs={}", carId, maintenanceDTOs);
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error updating maintenance: {}, Parameters: carId={}, maintenanceDTOs={}", e.getMessage(), carId, maintenanceDTOs);
            throw new ServiceException("Error updating car");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @CachePut(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('car_with_details_', #carId)")
    public void updateCarAndDetails(
            Long carId,
            List<CarIncidentsDTO> incidentsDTOs,
            List<KilometersDTO> kilometersDTOs,
            List<MaintenanceHistoryDTO> maintenanceDTOs) {
        try {

            List<CarIncidents> incidents = incidentsDTOs.stream()
                    .map(this::mapToCarIncidentsEntity)
                    .toList();
            List<Kilometers> kilometers = kilometersDTOs.stream()
                    .map(this::mapToKilometersEntity)
                    .toList();

            List<MaintenanceHistory> maintenance = maintenanceDTOs.stream()
                    .map(this::mapToMaintenanceEntity)
                    .toList();

            kilometersRepository.saveAll(kilometers);

            maintenanceRepository.saveAll(maintenance);

            carIncidentsRepository.saveAll(incidents);

        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: carId={}, carDTO={}, incidentsDTOs={}, kilometersDTOs={}, maintenanceDTOs={}", carId, incidentsDTOs, kilometersDTOs, maintenanceDTOs);
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error updating car and details: {}, Parameters: carId={}, carDTO={}, incidentsDTOs={}, kilometersDTOs={}, maintenanceDTOs={}", e.getMessage(), carId, incidentsDTOs, kilometersDTOs, maintenanceDTOs);
            throw new ServiceException("Error updating car");
        }
    }

    private CarDTO mapToCarDTO(Car car) {
        return CarDTO.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .licensePlate(car.getLicensePlate())
                .fabricationYear(car.getFabricationYear())
                .state(car.getState())
                .build();
    }

    private CarIncidentsDTO mapToCarIncidentsDTO(CarIncidents carIncidents) {
        return CarIncidentsDTO.builder()
                .id(carIncidents.getId())
                .description(carIncidents.getDescription())
                .createdAt(carIncidents.getCreatedAt())
                .type(carIncidents.getType())
                .carId(carIncidents.getCar().getId())
                .build();
    }

    private KilometersDTO mapToKilometersDTO(Kilometers kilometers) {
        return KilometersDTO.builder()
                .id(kilometers.getId())
                .actualKm(kilometers.getActualKm())
                .createdAt(kilometers.getCreatedAt())
                .carId(kilometers.getCar().getId())
                .build();
    }

    private MaintenanceHistoryDTO mapToMaintenanceDTO(MaintenanceHistory maintenance) {
        return MaintenanceHistoryDTO.builder()
                .id(maintenance.getId())
                .createdAt(maintenance.getCreatedAt())
                .description(maintenance.getDescription())
                .cost(maintenance.getCost())
                .type(maintenance.getType())
                .carId(maintenance.getCar().getId())
                .build();
    }

    private CarIncidents mapToCarIncidentsEntity(CarIncidentsDTO carIncidentsDTO) {
        Car car = carRepository.findById(carIncidentsDTO.getCarId()).orElseThrow(() -> new NotFoundException(NOTFOUND));

        return CarIncidents.builder()
                .id(carIncidentsDTO.getId())
                .description(carIncidentsDTO.getDescription())
                .createdAt(carIncidentsDTO.getCreatedAt())
                .type(carIncidentsDTO.getType())
                .car(car)
                .build();
    }

    private Kilometers mapToKilometersEntity(KilometersDTO kilometersDTO) {
        Car car = carRepository.findById(kilometersDTO.getCarId()).orElseThrow(() -> new NotFoundException(NOTFOUND));

        return Kilometers.builder()
                .id(kilometersDTO.getId())
                .actualKm(kilometersDTO.getActualKm())
                .createdAt(kilometersDTO.getCreatedAt())
                .car(car)
                .build();
    }

    private MaintenanceHistory mapToMaintenanceEntity(MaintenanceHistoryDTO maintenanceDTO) {
        Car car = carRepository.findById(maintenanceDTO.getCarId()).orElseThrow(() -> new NotFoundException(NOTFOUND));

        return MaintenanceHistory.builder()
                .id(maintenanceDTO.getId())
                .createdAt(maintenanceDTO.getCreatedAt())
                .description(maintenanceDTO.getDescription())
                .cost(maintenanceDTO.getCost())
                .type(maintenanceDTO.getType())
                .car(car)
                .build();
    }
}
