package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.CarIncidentsDTO;
import com.proyecto.flotavehicular_webapp.models.CarIncidents;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;

public interface ICarIncidentsService {
    PageResponse<CarIncidentsDTO> getAllIncidents(int pageNumber, int pageSize);

    CarIncidentsDTO getIncidentById(Long id);

    CarIncidents saveIncident(CarIncidentsDTO carIncidentsDTO);

    void updateIncident(Long id, CarIncidentsDTO carIncidentsDTO);

    void deleteIncident(Long id);

    PageResponse<CarIncidentsDTO> getIncidentsByCarId(Long id, int pageNumber, int pageSize);
}
