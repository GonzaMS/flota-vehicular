package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.CarIncidentsDTO;
import com.proyecto.flotavehicular_webapp.models.CarIncidents;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;

import java.util.Date;

public interface ICarIncidentsService {
    PageResponse<CarIncidentsDTO> getAll(int pageNumber, int pageSize);

    CarIncidentsDTO getById(Long id);

    CarIncidents save(CarIncidentsDTO carIncidentsDTO);

    CarIncidentsDTO update(Long id, CarIncidentsDTO carIncidentsDTO);

    void delete(Long id);

    // Filter
    PageResponse<CarIncidentsDTO> getByCarId(Long id, int pageNumber, int pageSize);

    PageResponse<CarIncidentsDTO> getByDate(Date startDate, Date endDate, int pageNumber, int pageSize);
}
