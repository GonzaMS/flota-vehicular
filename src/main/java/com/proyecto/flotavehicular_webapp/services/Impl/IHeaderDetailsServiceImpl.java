package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.*;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.*;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.repositories.ICarIncidentsRepository;
import com.proyecto.flotavehicular_webapp.repositories.IKilometersRepository;
import com.proyecto.flotavehicular_webapp.repositories.IMaintenanceRepository;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IHeaderDetailsServiceImpl {

    private final ICarRepository carRepository;
    private final ICarIncidentsRepository carIncidentsRepository;
    private final IKilometersRepository kilometersRepository;
    private final IMaintenanceRepository maintenanceRepository;

    private static final Logger logger = LoggerFactory.getLogger(IHeaderDetailsServiceImpl.class);

    private static final String NOTFOUND = "Car not found";

    public IHeaderDetailsServiceImpl(ICarRepository carRepository, ICarIncidentsRepository carIncidentsRepository,
                                     IKilometersRepository kilometersRepository, IMaintenanceRepository maintenanceRepository) {
        this.carRepository = carRepository;
        this.carIncidentsRepository = carIncidentsRepository;
        this.kilometersRepository = kilometersRepository;
        this.maintenanceRepository = maintenanceRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('car_with_details_', #id)")
    public CarWithDetailsDTO getCarWithDetails(Long id, int pageNumber, int pageSize) {
        try {
            Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));

            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<CarIncidents> incidentsPage = carIncidentsRepository.findByCar_CarId(id, pageable);
            Page<Kilometers> kilometersPage = kilometersRepository.findByCar_CarId(id, pageable);
            Page<MaintenanceHistory> maintenancePage = maintenanceRepository.findByCar_CarId(id, pageable);

            CarDTO carDTO = mapToCarDTO(car);
            List<CarIncidentsDTO> incidentDTOs = incidentsPage.stream().map(this::mapToCarIncidentsDTO).toList();
            List<KilometersDTO> kilometersDTOs = kilometersPage.stream().map(this::mapToKilometersDTO).toList();
            List<MaintenanceDTO> maintenanceDTOs = maintenancePage.stream().map(this::mapToMaintenanceDTO).toList();

            return CarWithDetailsDTO.builder()
                    .car(carDTO)
                    .incidents(incidentDTOs)
                    .kilometers(kilometersDTOs)
                    .maintenance(maintenanceDTOs)
                    .build();

        } catch (NotFoundException e) {
            logger.error("Details not found for car id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error updating car: {}", e.getMessage());
            throw new ServiceException("Error updating car");
        }
    }

    @Transactional
    @CacheEvict(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('car_with_details_', #carId)")
    public void updateIncidents(Long carId, List<CarIncidentsDTO> incidentsDTOs) {
        try {
            List<CarIncidents> incidents = incidentsDTOs.stream().map(this::mapToCarIncidentsEntity).toList();
            carIncidentsRepository.saveAll(incidents);

        } catch (NotFoundException e) {
            logger.error("Incident with id {} not found", carId);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating car: {}", e.getMessage());
            throw new ServiceException("Error updating car");
        }
    }

    @Transactional
    @CacheEvict(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('car_with_details_', #carId)")
    public void updateKilometers(Long carId, List<KilometersDTO> kilometersDTOs) {
        try {
            List<Kilometers> kilometers = kilometersDTOs.stream().map(this::mapToKilometersEntity).toList();
            kilometersRepository.saveAll(kilometers);

        } catch (NotFoundException e) {
            logger.error("Car with id {} not found", carId);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating car: {}", e.getMessage());
            throw new ServiceException("Error updating car");
        }
    }

    private CarDTO mapToCarDTO(Car car) {
        return CarDTO.builder()
                .carId(car.getCarId())
                .carBrand(car.getCarBrand())
                .carModel(car.getCarModel())
                .carLicensePlate(car.getCarLicensePlate())
                .carFabricationYear(car.getCarFabricationYear())
                .carState(car.getCarState())
                .build();
    }

    private CarIncidentsDTO mapToCarIncidentsDTO(CarIncidents carIncidents) {
        return CarIncidentsDTO.builder()
                .incidentId(carIncidents.getIncidentId())
                .incidentDescription(carIncidents.getIncidentDescription())
                .incidentDate(carIncidents.getIncidentDate())
                .incidentType(carIncidents.getIncidentType())
                .carId(carIncidents.getCar().getCarId())
                .build();
    }

    private KilometersDTO mapToKilometersDTO(Kilometers kilometers) {
        return KilometersDTO.builder()
                .kilometersId(kilometers.getKilometersId())
                .actualKm(kilometers.getActualKm())
                .updateKmDate(kilometers.getUpdateKmDate())
                .carId(kilometers.getCar().getCarId())
                .build();
    }

    private MaintenanceDTO mapToMaintenanceDTO(MaintenanceHistory maintenance) {
        return MaintenanceDTO.builder()
                .maintenanceId(maintenance.getMaintenanceId())
                .maintenanceDate(maintenance.getMaintenanceDate())
                .maintenanceDescription(maintenance.getMaintenanceDescription())
                .maintenanceCost(maintenance.getMaintenanceCost())
                .maintenanceType(maintenance.getMaintenanceType())
                .carId(maintenance.getCar().getCarId())
                .build();
    }

    private CarIncidents mapToCarIncidentsEntity(CarIncidentsDTO carIncidentsDTO) {
        Car car = carRepository.findById(carIncidentsDTO.getCarId()).orElseThrow(() -> new NotFoundException(NOTFOUND));

        return CarIncidents.builder()
                .incidentId(carIncidentsDTO.getIncidentId())
                .incidentDescription(carIncidentsDTO.getIncidentDescription())
                .incidentDate(carIncidentsDTO.getIncidentDate())
                .incidentType(carIncidentsDTO.getIncidentType())
                .car(car)
                .build();
    }

    private Kilometers mapToKilometersEntity(KilometersDTO kilometersDTO) {
        Car car = carRepository.findById(kilometersDTO.getCarId()).orElseThrow(() -> new NotFoundException(NOTFOUND));

        return Kilometers.builder()
                .kilometersId(kilometersDTO.getKilometersId())
                .actualKm(kilometersDTO.getActualKm())
                .updateKmDate(kilometersDTO.getUpdateKmDate())
                .car(car)
                .build();
    }
}
