package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.MaintenanceDTO;
import com.proyecto.flotavehicular_webapp.models.MaintenanceHistory;
import com.proyecto.flotavehicular_webapp.services.IMaintenanceService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/maintenances")
public class MaintenanceController {

    private final IMaintenanceService maintenanceService;

    public MaintenanceController(IMaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<MaintenanceDTO>> getAllMaintenances(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

            PageResponse<MaintenanceDTO> maintenanceDTOPageResponse = maintenanceService.getAllMaintenances(pageNumber,pageSize);

            if (maintenanceDTOPageResponse.items().isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(maintenanceDTOPageResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceDTO> getMaintenanceById(@PathVariable Long id){
        MaintenanceDTO maintenance = maintenanceService.getById(id);
        return ResponseEntity.ok(maintenance);
    }

    @PostMapping
    public ResponseEntity<MaintenanceHistory> saveMaintenance(@Valid @RequestBody MaintenanceDTO maintenanceDTO){
        MaintenanceHistory newMaintenance = maintenanceService.saveMaintenance(maintenanceDTO);

        if(newMaintenance == null){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(newMaintenance);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceDTO> updateMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceDTO maintenanceDTO){

        maintenanceService.updateMaintenance(id, maintenanceDTO);

        MaintenanceDTO updatedMaintenance = maintenanceService.getById(id);

        return ResponseEntity.ok().body(updatedMaintenance);
    }

    @DeleteMapping
    public ResponseEntity<MaintenanceHistory> deleteMaintenance(Long id){
        maintenanceService.deleteMaintenance(id);
        return ResponseEntity.ok().build();
    }

    // Filters
    @GetMapping("/car/{carId}")
    public ResponseEntity<PageResponse<MaintenanceDTO>> getMaintenancesByCarId(
            @PathVariable Long carId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize){

        PageResponse<MaintenanceDTO> maintenanceDTOPageResponse = maintenanceService.getMaintenanceByCarId(carId, pageNumber, pageSize);

        if (maintenanceDTOPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(maintenanceDTOPageResponse);
    }
}
