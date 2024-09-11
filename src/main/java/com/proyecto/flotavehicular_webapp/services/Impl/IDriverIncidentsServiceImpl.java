package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.DriverIncidentsDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Driver;
import com.proyecto.flotavehicular_webapp.models.DriverIncidents;
import com.proyecto.flotavehicular_webapp.repositories.IDriverIncidentsRepository;
import com.proyecto.flotavehicular_webapp.repositories.IDriverRepository;
import com.proyecto.flotavehicular_webapp.services.IDriverIncidentsService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IDriverIncidentsServiceImpl implements IDriverIncidentsService {

    private final IDriverIncidentsRepository incidentRecordRepository;
    private final IDriverRepository driverRepository;

    private static final String NOTFOUND = "Incident record not found";
    private static final String DRIVER_NOT_FOUND = "Driver not found";

    public IDriverIncidentsServiceImpl(IDriverIncidentsRepository incidentRecordRepository, IDriverRepository driverRepository) {
        this.incidentRecordRepository = incidentRecordRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DriverIncidentsDTO getIncidentRecordById(Long id) {
        DriverIncidents incidentRecord = incidentRecordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(incidentRecord);
    }

    @Override
    @Transactional
    public DriverIncidents saveIncidentRecord(DriverIncidentsDTO driverIncidentsDTO) {
        Driver driver = driverRepository.findById(driverIncidentsDTO.getDriverId())
                .orElseThrow(() -> new NotFoundException(DRIVER_NOT_FOUND));

        DriverIncidents incidentRecord = mapToEntity(driverIncidentsDTO);
        incidentRecord.setDriver(driver);
        return incidentRecordRepository.save(incidentRecord);
    }

    @Override
    @Transactional
    public void updateIncidentRecord(Long id, DriverIncidentsDTO driverIncidentsDTO) {
        DriverIncidents incidentRecord = incidentRecordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOTFOUND));

        Driver driver = driverRepository.findById(driverIncidentsDTO.getDriverId())
                .orElseThrow(() -> new NotFoundException(DRIVER_NOT_FOUND));

        incidentRecord.setIncidentDescription(driverIncidentsDTO.getIncidentDescription());
        incidentRecord.setIncidentDate(driverIncidentsDTO.getIncidentDate());
        incidentRecord.setIncidentType(driverIncidentsDTO.getIncidentType());
        incidentRecord.setDriver(driver);

        incidentRecordRepository.save(incidentRecord);
    }

    @Override
    @Transactional
    public void deleteIncidentRecord(Long id) {
        DriverIncidents driverIncidents = incidentRecordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOTFOUND));
        incidentRecordRepository.delete(driverIncidents);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DriverIncidentsDTO> getAllIncidentRecords(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<DriverIncidents> incidentRecordPage = incidentRecordRepository.findAll(pageable);

        List<DriverIncidentsDTO> driverIncidentsDTOList = incidentRecordPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                driverIncidentsDTOList,
                incidentRecordPage.getNumber(),
                incidentRecordPage.getSize(),
                incidentRecordPage.getTotalElements(),
                incidentRecordPage.getTotalPages(),
                incidentRecordPage.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DriverIncidentsDTO> getIncidentRecordsByDriver(Long driverId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<DriverIncidents> incidentRecordPage = incidentRecordRepository.findByDriver_DriverId(driverId, pageable);

        List<DriverIncidentsDTO> driverIncidentsDTOList = incidentRecordPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                driverIncidentsDTOList,
                incidentRecordPage.getNumber(),
                incidentRecordPage.getSize(),
                incidentRecordPage.getTotalElements(),
                incidentRecordPage.getTotalPages(),
                incidentRecordPage.isLast());
    }

    private DriverIncidents mapToEntity(DriverIncidentsDTO driverIncidentsDTO) {
        return DriverIncidents.builder()
                .incidentId(driverIncidentsDTO.getIncidentId())
                .incidentDescription(driverIncidentsDTO.getIncidentDescription())
                .incidentDate(driverIncidentsDTO.getIncidentDate())
                .incidentType(driverIncidentsDTO.getIncidentType())
                .build();
    }

    private DriverIncidentsDTO mapToDTO(DriverIncidents incidentRecord) {
        return DriverIncidentsDTO.builder()
                .incidentId(incidentRecord.getIncidentId())
                .incidentDescription(incidentRecord.getIncidentDescription())
                .incidentDate(incidentRecord.getIncidentDate())
                .incidentType(incidentRecord.getIncidentType())
                .driverId(incidentRecord.getDriver().getDriverId())
                .build();
    }
}
