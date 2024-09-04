package com.proyecto.flotavehicular_webapp.services;


import com.proyecto.flotavehicular_webapp.dto.MaintenanceDTO;
import com.proyecto.flotavehicular_webapp.models.MaintenanceHistory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IMaintenanceService {
    List<MaintenanceDTO> getAllMaintenances();

    MaintenanceDTO getById(Long id);
}
