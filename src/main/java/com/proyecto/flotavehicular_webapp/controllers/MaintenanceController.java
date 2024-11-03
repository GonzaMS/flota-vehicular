package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.car.MaintenanceHistoryDTO;
import com.proyecto.flotavehicular_webapp.models.Car.MaintenanceHistory;
import com.proyecto.flotavehicular_webapp.services.IMaintenanceService;
import com.proyecto.flotavehicular_webapp.utils.DateRange;
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
    public ResponseEntity<PageResponse<MaintenanceHistoryDTO>> getAllMaintenances(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<MaintenanceHistoryDTO> maintenanceDTOPageResponse = maintenanceService.getAll(pageNumber, effectivePageSize);

        return ResponseEntity.ok(maintenanceDTOPageResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceHistoryDTO> getMaintenanceById(@PathVariable Long id) {
        MaintenanceHistoryDTO maintenance = maintenanceService.getById(id);
        return ResponseEntity.ok(maintenance);
    }

    @PostMapping
    public ResponseEntity<MaintenanceHistory> saveMaintenance(@Valid @RequestBody MaintenanceHistoryDTO maintenanceDTO) {
        MaintenanceHistory newMaintenance = maintenanceService.save(maintenanceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMaintenance);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceHistoryDTO> updateMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceHistoryDTO maintenanceDTO) {

        maintenanceService.update(id, maintenanceDTO);

        MaintenanceHistoryDTO updatedMaintenance = maintenanceService.getById(id);

        return ResponseEntity.ok().body(updatedMaintenance);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteMaintenance(@PathVariable Long id) {
        maintenanceService.delete(id);
        return ResponseEntity.ok().build();
    }

    // Filters
    @GetMapping("/car/{carId}")
    public ResponseEntity<PageResponse<MaintenanceHistoryDTO>> getMaintenancesByCarId(
            @PathVariable Long carId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<MaintenanceHistoryDTO> maintenanceDTOPageResponse = maintenanceService.getByCarId(carId, pageNumber, effectivePageSize);

        return ResponseEntity.ok(maintenanceDTOPageResponse);
    }

    @PostMapping("/date")
    public ResponseEntity<PageResponse<MaintenanceHistoryDTO>> getKilometersByDate(
            @RequestBody @Valid DateRange dateRange,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<MaintenanceHistoryDTO> maintenanceHistoryDTOPageResponse = maintenanceService.getByDate(dateRange.getStartDate(), dateRange.getEndDate(), pageNumber, effectivePageSize);

        return ResponseEntity.ok(maintenanceHistoryDTOPageResponse);
    }

    @PostMapping("/car/{carId}/date")
    public ResponseEntity<PageResponse<MaintenanceHistoryDTO>> getKilometersByCarIdAndDate(
            @PathVariable Long carId,
            @RequestBody @Valid DateRange dateRange,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<MaintenanceHistoryDTO> maintenanceHistoryDTOPageResponse = maintenanceService.getByCarIdAndDate(carId, dateRange.getStartDate(), dateRange.getEndDate(), pageNumber, effectivePageSize);

        return ResponseEntity.ok(maintenanceHistoryDTOPageResponse);
    }
}
