package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.MaintenanceDTO;
import com.proyecto.flotavehicular_webapp.services.IMaintenanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/maintenances")
public class MaintenanceController {

    private final IMaintenanceService maintenanceService;
    public MaintenanceController(IMaintenanceService maintenanceService){
        this.maintenanceService = maintenanceService;
    }


    @GetMapping
    public ResponseEntity<List<MaintenanceDTO>> getAllMaintenances(){
        List<MaintenanceDTO> maintenances = maintenanceService.getAllMaintenances();
        return ResponseEntity.ok(maintenances);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceDTO> getMaintenanceById(@PathVariable Long id){
        MaintenanceDTO maintenance = maintenanceService.getById(id);

        return ResponseEntity.ok(maintenance);
    }
}
