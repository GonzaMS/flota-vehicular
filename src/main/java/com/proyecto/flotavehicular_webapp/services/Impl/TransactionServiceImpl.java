package com.proyecto.flotavehicular_webapp.services.Impl;


import com.proyecto.flotavehicular_webapp.dto.driver.DriverDTO;
import com.proyecto.flotavehicular_webapp.dto.driver.DriverIncidentsDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Driver.Driver;
import com.proyecto.flotavehicular_webapp.models.Driver.DriverIncidents;
import com.proyecto.flotavehicular_webapp.repositories.IDriverIncidentsRepository;
import com.proyecto.flotavehicular_webapp.repositories.IDriverRepository;
import com.proyecto.flotavehicular_webapp.services.IDriverIncidentsService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class TransactionServiceImpl {

    private final Boolean fail = false;

    private final IDriverIncidentsRepository driverIncidentsRepository;
    private final IDriverRepository driverRepository;
    private final IDriverServiceImpl driverServiceImpl;
    private final IDriverIncidentsService driverIncidentsService;

    public TransactionServiceImpl(IDriverIncidentsRepository driverIncidentsRepository, IDriverRepository driverRepository, IDriverServiceImpl driverServiceImpl, IDriverIncidentsService driverIncidentsService) {
        this.driverIncidentsRepository = driverIncidentsRepository;
        this.driverRepository = driverRepository;
        this.driverServiceImpl = driverServiceImpl;
        this.driverIncidentsService = driverIncidentsService;
    }

    // Read only
    @Transactional(readOnly = true)
    public DriverIncidentsDTO getIncidentReadOnly(Long id) {
        log.info("Reading incident with read-only transaction - Parameters: IncidentID={}", id);

        DriverIncidents incident = driverIncidentsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Incident not found"));

        log.info("Incident found: {}", incident.getIncidentId());

        if (fail) {
            log.error("Failure triggered: throwing RuntimeException in getIncidentReadOnly");
            throw new RuntimeException("Simulated failure: getIncidentReadOnly");
        }

        return mapToDTO(incident);
    }

    // Required Direct
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = 5)
    public void updateIncidentDirect(Long id, String description) {
        try {
            log.info("Updating incident with REQUIRED propagation - Parameters: IncidentID={}, Description={}, fail={}", id, description, fail);

            DriverIncidents incident = driverIncidentsRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Incident not found"));

            incident.setIncidentDescription(description.trim());
            driverIncidentsRepository.save(incident);

            if (fail) {
                throw new RuntimeException("Simulated failure: updateIncidentMandatory");
            }

        } catch (Exception e) {
            log.error("Rollback triggered - Error updating incident: {}, Parameters: IncidentID={}, Description={}", e.getMessage(), id, description);
            throw new ServiceException("Error updating incident");
        }
    }

    // Required Indirect
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = 5)
    public void updateIncidentIndirect(Long driverId, Long incidentId, DriverDTO driverDTO, DriverIncidentsDTO incidentDTO) {
        try {
            log.info("Updating driver and incident with REQUIRED propagation - Parameters: DriverID={}, IncidentID={}, fail={}", driverId, incidentDTO, fail);

            driverServiceImpl.updateDriver(driverId, driverDTO);

            driverIncidentsService.updateIncidentRecord(incidentId, incidentDTO);

            if (fail) {
                throw new RuntimeException("Simulated failure: updateIncidentIndirect");
            }

        } catch (Exception e) {
            log.error("Error updating car and incident. Rolling back transaction. DriverID: {}, IncidentID: {}, driverDTO: {}, incidentDTO: {}", driverId, incidentId, driverDTO, incidentDTO, e);
            throw new ServiceException("Error updating driver and incident", e);
        }
    }

    // REQUIRES_NEW Direct
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 5)
    public Driver saveDriverDirect(DriverDTO driverDTO) {
        try {
            Driver driver = mapToEntity(driverDTO);
            if (fail) throw new RuntimeException("Simulated failure: saveDriver");
            return driverRepository.save(driver);
        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: driverDTO={}, driverID={}", driverDTO, driverDTO.getDriverId());
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error saving incident: {}, Parameters: driverIncidentsDTO={}, driverId={}", e.getMessage(), driverDTO, driverDTO.getDriverId());
            throw new ServiceException("Error saving car");
        }
    }

    // REQUIRES_NEW Indirect
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 5)
    public DriverIncidents saveIncidentIndirect(DriverIncidentsDTO incidentDTO) {
        try {
            log.info("Attempting to save incident (new transaction) - Parameters: IncidentDTO={}, fail={}", incidentDTO, fail);


            Driver driver = driverRepository.findById(incidentDTO.getDriverId())
                    .orElseThrow(() -> new NotFoundException("Car not found"));

            DriverIncidents incident = new DriverIncidents();
            incident.setIncidentDescription(incidentDTO.getIncidentDescription());
            incident.setIncidentType(incidentDTO.getIncidentType());
            incident.setDriver(driver);

            if (fail) {
                throw new RuntimeException("Simulated failure: saveNewTransaction");
            }

            driverIncidentsRepository.save(incident);

            return incident;
        } catch (Exception e) {
            log.error("Rollback triggered - Error saving incident: {}, Parameters: driverIncidentsDTO={}, driverId={}", e.getMessage(), incidentDTO, incidentDTO.getDriverId());
            throw new ServiceException("Error saving incident");
        }
    }

    // SUPPORTS Direct
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, timeout = 5)
    public DriverDTO getByIdDirect(Long id) {
        try {
            log.info("Reading incident with SUPPORTS - Parameters: IncidentID={}, fail={}", id, fail);

            Driver driver = driverRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Driver not found"));

            log.info("Incident found: {}", driver.getDriverName());

            if (fail) {
                throw new RuntimeException("Simulated failure: readIncidentSupports");
            }

            return mapToDTO(driver);
        } catch (Exception e) {
            log.error("Rollback triggered - Error reading driver: {}, Parameters: driverId={}", e.getMessage(), id);
            throw new ServiceException("Error reading incident");
        }

    }

    // SUPPORTS Indirect
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, timeout = 5)
    public PageResponse<DriverIncidentsDTO> geyByIdIndirect(Long id, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<DriverIncidents> driverIncidentsPage = driverIncidentsRepository.findByDriver_DriverId(id, pageable);

            if (driverIncidentsPage.isEmpty()) {
                throw new NotFoundException("driver with id " + id + " has no incidents");
            }

            if (fail) {
                throw new RuntimeException("Simulated failure: getByDriver");
            }

            return mapToPageResponse(driverIncidentsPage);
        } catch (Exception e) {
            log.error("Rollback triggered - Error getting incidents by car id: {}, Parameters: driverId={}, pageNumber={}, pageSize={}", id, pageNumber, pageSize, e.getMessage());
            throw new ServiceException("Error getting incidents by car id");
        }
    }

    // Not supported
    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = Exception.class, timeout = 5)
    public void deleteDriver (Long id) {
        try {
            log.info("Deleting incident without transaction - Parameters: IncidentID={}, fail={}", id, fail);


            if (fail) {
                throw new RuntimeException("Simulated failure: deleteIncidentNotSupported");
            }

            driverServiceImpl.deleteDriver(id);

        } catch (Exception e) {
            log.error("Rollback triggered - Error deactivating car: {}, Parameters: carId={}", e.getMessage(), id);
            throw new ServiceException("Error deactivating car");
        }
    }

    @Transactional(propagation = Propagation.NEVER, timeout = 5)
    public void sendNotificationNever(String email, String message) {
        try {
            log.info("Sending notification (NEVER transaction) - Parameters: Email={}, Message={}, fail={}", email, message, fail);

            if (fail) {
                throw new RuntimeException("Simulated failure: sendNotificationNever");
            }

            log.info("Notification sent successfully to {}", email);
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
            throw new ServiceException("Error sending notification");
        }
    }

    private DriverIncidentsDTO mapToDTO(DriverIncidents incidentRecord) {
        return DriverIncidentsDTO.builder()
                .incidentId(incidentRecord.getIncidentId())
                .incidentDescription(incidentRecord.getIncidentDescription())
                .createdAt(incidentRecord.getCreatedAt())
                .incidentType(incidentRecord.getIncidentType())
                .driverId(incidentRecord.getDriver().getDriverId())
                .build();
    }

    private Driver mapToEntity(DriverDTO driverDTO) {
        return Driver.builder()
                .driverId(driverDTO.getDriverId())
                .driverName(driverDTO.getDriverName())
                .driverLicense(driverDTO.getDriverLicense())
                .driverState(driverDTO.getDriverState())
                .driverLicenseExpirationDate(driverDTO.getDriverLicenseExpirationDate())
                .build();
    }

    private DriverDTO mapToDTO(Driver driver) {
        DriverDTO.DriverDTOBuilder builder = DriverDTO.builder()
                .driverId(driver.getDriverId())
                .driverLicense(driver.getDriverLicense())
                .driverName(driver.getDriverName())
                .driverState(driver.getDriverState())
                .driverLicenseExpirationDate(driver.getDriverLicenseExpirationDate());
        return builder.build();
    }

    private PageResponse<DriverIncidentsDTO> mapToPageResponse(Page<DriverIncidents> driverIncidentsPage) {
        List<DriverIncidentsDTO> driverIncidentsDTOList = driverIncidentsPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                driverIncidentsDTOList,
                driverIncidentsPage.getNumber(),
                driverIncidentsPage.getSize(),
                driverIncidentsPage.getTotalElements(),
                driverIncidentsPage.getTotalPages(),
                driverIncidentsPage.isLast());
    }
}
