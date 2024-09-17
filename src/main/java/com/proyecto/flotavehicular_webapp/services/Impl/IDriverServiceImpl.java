package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.CarDTO;
import com.proyecto.flotavehicular_webapp.dto.DriverDTO;
import com.proyecto.flotavehicular_webapp.dto.DriverIncidentsDTO;
import com.proyecto.flotavehicular_webapp.dto.DrivingHistoryDTO;
import com.proyecto.flotavehicular_webapp.dto.PerformanceEvaluationDTO;
import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.models.Driver;
import com.proyecto.flotavehicular_webapp.models.DriverIncidents;
import com.proyecto.flotavehicular_webapp.models.DrivingHistory;
import com.proyecto.flotavehicular_webapp.models.PerformanceEvaluation;
import com.proyecto.flotavehicular_webapp.repositories.IDriverRepository;
import com.proyecto.flotavehicular_webapp.services.IDriverService;
import com.proyecto.flotavehicular_webapp.utils.EnumUtils;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
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
public class IDriverServiceImpl implements IDriverService {

    private final IDriverRepository driverRepository;

    private static final String NOTFOUND = "Driver not found";

    private static final Logger logger = LoggerFactory.getLogger(IDriverServiceImpl.class);


    public IDriverServiceImpl(IDriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DriverDTO> getAllDrivers(int pageNumber, int pageSize) {

        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Driver> driverPage = driverRepository.findAll(pageable);

            return mapToPageResponse(driverPage,false);
        }catch (Exception e) {
                logger.error("Error getting all cars: {}", e.getMessage());
                throw new NotFoundException("Error getting all cars");
            }
    }

    @Override
    @Transactional(readOnly = true)
    public DriverDTO getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(driver);
    }

    @Override
    @Transactional
    public Driver saveDriver(DriverDTO driverDTO) {
        Driver driver = mapToEntity(driverDTO);
        return driverRepository.save(driver);
    }

    @Override
    @Transactional
    public void updateDriver(Long id, DriverDTO driverDTO) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Driver not found"));

        driver.setDriverName(driverDTO.getDriverName());
        driver.setDriverLicense(driverDTO.getDriverLicense());
        driver.setDriverLicenseExpirationDate(driverDTO.getDriverLicenseExpirationDate());
        driver.setDriverState(driverDTO.getDriverState());

        driverRepository.save(driver);
    }



    @Override
    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOTFOUND));
        driverRepository.delete(driver);
    }

    // Filters
    @Override
    @Transactional(readOnly = true)
    public PageResponse<DriverDTO> getDriverByName(String name, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Driver> driverPage = driverRepository.findByDriverName(name, pageable);

        if (driverPage.isEmpty()) {
            throw new NotFoundException("Name not found: " + name);
        }

        return mapToPageResponse(driverPage,false);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DriverDTO> getDriverByLicense(String license, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Driver> driverPage = driverRepository.findByDriverLicense(license, pageable);

        if (driverPage.isEmpty()) {
            throw new NotFoundException("License not found: " + license);
        }

        return mapToPageResponse(driverPage,false);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DriverDTO> getDriverByState(String state, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        if(!EnumUtils.isValidState(state)) {
            throw new NotFoundException("State not found: " + state);
        }
        ESTATES driverState = ESTATES.valueOf(state);
        Page<Driver> driverPage = driverRepository.findByDriverState(driverState, pageable);

        if (driverPage.isEmpty()) {
            throw new NotFoundException("State not found: " + state);
        }

         return mapToPageResponse(driverPage,false);
    }

    // Mappers
    private Driver mapToEntity(DriverDTO driverDTO) {
        return Driver.builder()
                .driverId(driverDTO.getDriverId())
                .driverName(driverDTO.getDriverName())
                .driverLicense(driverDTO.getDriverLicense())
                .driverState(driverDTO.getDriverState())
                .driverLicenseExpirationDate(driverDTO.getDriverLicenseExpirationDate())
                .drivingHistories(driverDTO.getDrivingHistories() != null ?
                        driverDTO.getDrivingHistories().stream()
                                .map(this::mapToDriverHistoryDTO)
                                .toList()
                        : Collections.emptyList())
                .driverIncidents(driverDTO.getDriverIncidents() != null ?
                        driverDTO.getDriverIncidents().stream()
                                .map(this::mapToDriverIncidentsDTO).
                                toList()
                        : Collections.emptyList())
                .evaluations(driverDTO.getEvaluations() != null ?
                        driverDTO.getEvaluations().stream()
                                .map(this::mapToPerformanceDTO)
                                .toList()
                        : Collections.emptyList())
                .build();
    }

    // Mapping DriverEvaluation to DTO
    private DrivingHistory mapToDriverHistoryDTO(DrivingHistoryDTO drivingHistoryDTO) {
        return DrivingHistory.builder()
                .drivingHistoryId(drivingHistoryDTO.getDrivingHistoryId())
                .driver(Driver.builder().driverId(drivingHistoryDTO.getDriverId()).build())
                .drivingDate(drivingHistoryDTO.getDrivingDate())
                .kmDriven(drivingHistoryDTO.getKmDriven())
                .build();
    }

    // Mapping DriverIncidents to DTO
    private DriverIncidents mapToDriverIncidentsDTO(DriverIncidentsDTO driverIncidentsDTO) {
        return DriverIncidents.builder()
                .incidentId(driverIncidentsDTO.getIncidentId())
                .driver(Driver.builder().driverId(driverIncidentsDTO.getDriverId()).build())
                .incidentDate(driverIncidentsDTO.getIncidentDate())
                .incidentDescription(driverIncidentsDTO.getIncidentDescription())
                .incidentType(driverIncidentsDTO.getIncidentType())
                .build();
    }

    // Mapping Performance to DTO
    private PerformanceEvaluation mapToPerformanceDTO(PerformanceEvaluationDTO performanceEvaluationDTO){
        return PerformanceEvaluation.builder()
                .performanceId(performanceEvaluationDTO.getPerformanceId())
                .driver(Driver.builder().driverId(performanceEvaluationDTO.getDriverId()).build())
                .performanceDate(performanceEvaluationDTO.getPerformanceDate())
                .performancePoints(performanceEvaluationDTO.getPerformancePoints())
                .build();
    }

    // Mapping the Car object to DTO
    private DriverDTO mapToDTO(Driver driver, boolean includeRelations) {
        DriverDTO.DriverDTOBuilder builder = DriverDTO.builder()
                .driverId(driver.getDriverId())
                .driverLicense(driver.getDriverLicense())
                .driverName(driver.getDriverName())
                .driverState(driver.getDriverState())
                .driverLicenseExpirationDate(driver.getDriverLicenseExpirationDate());

        if (includeRelations) {
            builder.drivingHistories(driver.getDrivingHistories() != null ?
                            driver.getDrivingHistories().stream().map(this::mapToMaintenanceEntity).toList() : Collections.emptyList())
                    .driverIncidents(driver.getDriverIncidents() != null ?
                            driver.getDriverIncidents().stream().map(this::mapDriverIncidentsEntity).toList() : Collections.emptyList())
                    .evaluations(driver.getEvaluations() != null ?
                            driver.getEvaluations().stream().map(this::mapToPerformanceEntity).toList() : Collections.emptyList());
        }
        return builder.build();
    }

    // Mapping DrivingHistory dto to Entity
    private DrivingHistoryDTO mapToMaintenanceEntity(DrivingHistory drivingHistory) {
        return DrivingHistoryDTO.builder()
                .drivingHistoryId(drivingHistory.getDrivingHistoryId())
                .drivingDate(drivingHistory.getDrivingDate())
                .kmDriven(drivingHistory.getKmDriven())
                .build();
    }

    // Mapping CarDTO dto to Entity
    private DriverIncidentsDTO  mapDriverIncidentsEntity(DriverIncidents driverIncidents) {
        return DriverIncidentsDTO.builder()
                .incidentId(driverIncidents.getIncidentId())
                .incidentDescription(driverIncidents.getIncidentDescription())
                .incidentDate(driverIncidents.getIncidentDate())
                .incidentType(driverIncidents.getIncidentType())
                .build();
    }

    // Mapping PerformanceEvaluation dto to Entity
    private PerformanceEvaluationDTO mapToPerformanceEntity(PerformanceEvaluation performanceEvaluation) {
        return PerformanceEvaluationDTO.builder()
                .performanceId(performanceEvaluation.getPerformanceId())
                .performanceDate(performanceEvaluation.getPerformanceDate())
                .performancePoints(performanceEvaluation.getPerformancePoints())
                .build();
    }

    // Page Response
    private PageResponse<DriverDTO> mapToPageResponse(Page<Driver> carPage, Boolean includeRelations) {
        List<DriverDTO> driverDTOList = carPage.stream()
                .map(driver -> mapToDTO(driver, includeRelations))
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