package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.DriverIncidentsDTO;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.models.DriverIncidents;


public interface IDriverIncidentsService {

    DriverIncidentsDTO getIncidentRecordById(Long id);

    DriverIncidents saveIncidentRecord(DriverIncidentsDTO driverIncidentsDTO);

    void updateIncidentRecord(Long id, DriverIncidentsDTO driverIncidentsDTO);

    void deleteIncidentRecord(Long id);

    PageResponse<DriverIncidentsDTO> getAllIncidentRecords(int pageNumber, int pageSize);

    PageResponse<DriverIncidentsDTO> getIncidentRecordsByDriver(Long driverId, int pageNumber, int pageSize);
}
