package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.DrivingHistoryDTO;
import com.proyecto.flotavehicular_webapp.models.DrivingHistory;
import com.proyecto.flotavehicular_webapp.services.IDrivingHistoryService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/driving-history")
public class DrivingHistoryController {

    private final IDrivingHistoryService drivingHistoryService;

    public DrivingHistoryController(IDrivingHistoryService drivingHistoryService) {
        this.drivingHistoryService = drivingHistoryService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<DrivingHistoryDTO>> getAllDrivingHistories(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<DrivingHistoryDTO> drivingHistories = drivingHistoryService.getAllDrivingHistories(pageNumber, pageSize);

        if(drivingHistories.items().isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(drivingHistories);
    }


    @GetMapping("/{id}")
    public ResponseEntity<DrivingHistoryDTO> getDrivingHistoryById(@PathVariable("id") Long id) {
        DrivingHistoryDTO drivingHistory = drivingHistoryService.getDrivingHistoryById(id);
        return ResponseEntity.ok(drivingHistory);
    }

    @PostMapping
    public ResponseEntity<DrivingHistory> saveDrivingHistory(@Valid @RequestBody DrivingHistoryDTO drivingHistoryDTO) {
        DrivingHistory newdrivingHistory =drivingHistoryService.saveDrivingHistory(drivingHistoryDTO);
        if (newdrivingHistory == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(newdrivingHistory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DrivingHistoryDTO> updateDrivingHistory(
            @PathVariable("id") Long id,
            @Valid @RequestBody DrivingHistoryDTO drivingHistoryDTO) {

        drivingHistoryService.updateDrivingHistory(id, drivingHistoryDTO);

        DrivingHistoryDTO updatedDrivingHistory = drivingHistoryService.getDrivingHistoryById(id);
        return ResponseEntity.ok().body(updatedDrivingHistory);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<DrivingHistory> deleteDrivingHistory(@PathVariable Long id) {
        drivingHistoryService.deleteDrivingHistory(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-driver/{driverId}")
    public ResponseEntity<PageResponse<DrivingHistoryDTO>> getDrivingHistoryByDriverId(
            @PathVariable Long driverId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<DrivingHistoryDTO> drivingHistories = drivingHistoryService.getDrivingHistoryByDriverId(driverId, pageNumber, pageSize);
        if(drivingHistories.items().isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(drivingHistories);
    }

    @GetMapping("/by-car/{carId}")
    public ResponseEntity<PageResponse<DrivingHistoryDTO>> getDrivingHistoryByCarId(
            @PathVariable("carId") Long carId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<DrivingHistoryDTO> drivingHistories = drivingHistoryService.getDrivingHistoryByCarId(carId, pageNumber, pageSize);
        if(drivingHistories.items().isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(drivingHistories);
    }
}
