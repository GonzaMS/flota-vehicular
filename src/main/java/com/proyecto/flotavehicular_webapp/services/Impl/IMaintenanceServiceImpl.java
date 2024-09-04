package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.MaintenanceDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.MaintenanceHistory;
import com.proyecto.flotavehicular_webapp.repositories.IMaintenanceRepository;
import com.proyecto.flotavehicular_webapp.services.IMaintenanceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IMaintenanceServiceImpl implements IMaintenanceService {

    private final IMaintenanceRepository maintenanceRepository;
    private static final String NOTFOUND  = "Maintenance not found";

    public IMaintenanceServiceImpl(IMaintenanceRepository maintenanceRepository){
        this.maintenanceRepository = maintenanceRepository;
    }

    @Override
    public List<MaintenanceDTO> getAllMaintenances() {
        List<MaintenanceHistory> maintenanceHistories = maintenanceRepository.findAll();
        return maintenanceHistories.stream().map(this::mapToDto).toList();
    }

    @Override
    public MaintenanceDTO getById(Long id) {
        MaintenanceHistory maintenanceHistory = maintenanceRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));

        return mapToDto(maintenanceHistory);
    }


    private MaintenanceDTO mapToDto(MaintenanceHistory maintenanceHistory){
        return MaintenanceDTO.builder()
                .maintenanceId(maintenanceHistory.getMaintenanceId())
                .serviceDate(maintenanceHistory.getMaintenanceDate())
                .serviceDescription(maintenanceHistory.getMaintenanceDescription())
                .serviceCost(maintenanceHistory.getMaintenanceCost())
                .serviceType(maintenanceHistory.getMaintenanceType())
                .build();
    }
}
