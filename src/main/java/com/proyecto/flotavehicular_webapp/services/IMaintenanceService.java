package com.proyecto.flotavehicular_webapp.services;


import com.proyecto.flotavehicular_webapp.dto.MaintenanceDTO;
import com.proyecto.flotavehicular_webapp.models.MaintenanceHistory;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;


public interface IMaintenanceService {
    PageResponse<MaintenanceDTO> getAllMaintenances(int pageNumber, int pageSize);

    MaintenanceDTO getById(Long id);

    MaintenanceHistory saveMaintenance(MaintenanceDTO maintenanceDTO);

    void updateMaintenance(Long id, MaintenanceDTO maintenanceDTO);

    void deleteMaintenance(Long id);

    PageResponse getMaintenanceByCarId(Long id, int pageNumber, int pageSize);
}
