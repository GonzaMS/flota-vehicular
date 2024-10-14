package com.proyecto.flotavehicular_webapp.services;


import com.proyecto.flotavehicular_webapp.dto.car.MaintenanceHistoryDTO;
import com.proyecto.flotavehicular_webapp.models.Car.MaintenanceHistory;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;


public interface IMaintenanceService {
    PageResponse<MaintenanceHistoryDTO> getAll(int pageNumber, int pageSize);

    MaintenanceHistoryDTO getById(Long id);

    MaintenanceHistory save(MaintenanceHistoryDTO maintenanceDTO);

    MaintenanceHistoryDTO update(Long id, MaintenanceHistoryDTO maintenanceDTO);

    void delete(Long id);

    // Filter
    PageResponse<MaintenanceHistoryDTO> getByCarId(Long id, int pageNumber, int pageSize);
}
