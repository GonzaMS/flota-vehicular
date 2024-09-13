package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.MaintenanceDTO;
import com.proyecto.flotavehicular_webapp.models.MaintenanceHistory;
import com.proyecto.flotavehicular_webapp.services.IMaintenanceService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/maintenances")
public class MaintenanceController {

    @Value("${page.size}")
    private int defaultPageSize;

    private final IMaintenanceService maintenanceService;

    public MaintenanceController(IMaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<MaintenanceDTO>> getAllMaintenances(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<MaintenanceDTO> maintenanceDTOPageResponse = maintenanceService.getAll(pageNumber, effectivePageSize);

        if (maintenanceDTOPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(maintenanceDTOPageResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceDTO> getMaintenanceById(@PathVariable Long id) {
        MaintenanceDTO maintenance = maintenanceService.getById(id);
        return ResponseEntity.ok(maintenance);
    }

    @PostMapping
    public ResponseEntity<MaintenanceHistory> saveMaintenance(@Valid @RequestBody MaintenanceDTO maintenanceDTO) {
        MaintenanceHistory newMaintenance = maintenanceService.save(maintenanceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMaintenance);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceDTO> updateMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceDTO maintenanceDTO) {

        maintenanceService.update(id, maintenanceDTO);

        MaintenanceDTO updatedMaintenance = maintenanceService.getById(id);

        return ResponseEntity.ok().body(updatedMaintenance);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MaintenanceHistory> deleteMaintenance(@PathVariable Long id) {
        maintenanceService.delete(id);
        return ResponseEntity.ok().build();
    }

    // Filters
    @GetMapping("/car/{carId}")
    public ResponseEntity<PageResponse<MaintenanceDTO>> getMaintenancesByCarId(
            @PathVariable Long carId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<MaintenanceDTO> maintenanceDTOPageResponse = maintenanceService.getByCarId(carId, pageNumber, effectivePageSize);

        if (maintenanceDTOPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(maintenanceDTOPageResponse);
    }
}
