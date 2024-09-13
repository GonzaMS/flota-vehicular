package com.proyecto.flotavehicular_webapp.services;


import com.proyecto.flotavehicular_webapp.dto.MaintenanceDTO;
import com.proyecto.flotavehicular_webapp.models.MaintenanceHistory;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;


public interface IMaintenanceService {
    PageResponse<MaintenanceDTO> getAll(int pageNumber, int pageSize);

    MaintenanceDTO getById(Long id);

    MaintenanceHistory save(MaintenanceDTO maintenanceDTO);

    void update(Long id, MaintenanceDTO maintenanceDTO);

    void delete(Long id);

    PageResponse<MaintenanceDTO> getByCarId(Long id, int pageNumber, int pageSize);
}
