package com.proyecto.flotavehicular_webapp.services;


import com.proyecto.flotavehicular_webapp.dto.MaintenanceDTO;
import com.proyecto.flotavehicular_webapp.models.MaintenanceHistory;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;


public interface IMaintenanceService {
    PageResponse<MaintenanceDTO> getAllMaintenances(int pageNumber, int pageSize);

    MaintenanceDTO getMaintenanceById(Long id);

    MaintenanceHistory saveMaintenance(MaintenanceDTO maintenanceDTO);

    void updateMaintenance(Long id, MaintenanceDTO maintenanceDTO);

    void deleteMaintenance(Long id);

    PageResponse<MaintenanceDTO> getMaintenanceByCarId(Long id, int pageNumber, int pageSize);
}
