package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.driver.DrivingHistoryDTO;
import com.proyecto.flotavehicular_webapp.models.Driver.DrivingHistory;
import com.proyecto.flotavehicular_webapp.services.IDrivingHistoryService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/driving-history")
public class DrivingHistoryController {

    private final IDrivingHistoryService drivingHistoryService;

    public DrivingHistoryController(IDrivingHistoryService drivingHistoryService) {
        this.drivingHistoryService = drivingHistoryService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<DrivingHistoryDTO>> getAllDrivingHistories(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<DrivingHistoryDTO> drivingHistories = drivingHistoryService.getAllDrivingHistories(pageNumber, pageSize);


        return ResponseEntity.ok(drivingHistories);
    }


    @GetMapping("/{id}")
    public ResponseEntity<DrivingHistoryDTO> getDrivingHistoryById(@PathVariable("id") Long id) {
        DrivingHistoryDTO drivingHistory = drivingHistoryService.getDrivingHistoryById(id);
        return ResponseEntity.ok(drivingHistory);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DrivingHistory> saveDrivingHistory(
            @RequestBody DrivingHistoryDTO drivingHistoryDTO,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        DrivingHistory savedHistory = drivingHistoryService.saveDrivingHistory(drivingHistoryDTO, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHistory);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DrivingHistoryDTO> updateDrivingHistory(
            @PathVariable("id") Long id,
            @Valid @RequestBody DrivingHistoryDTO drivingHistoryDTO) {

        drivingHistoryService.updateDrivingHistory(id, drivingHistoryDTO);

        DrivingHistoryDTO updatedDrivingHistory = drivingHistoryService.getDrivingHistoryById(id);
        return ResponseEntity.ok().body(updatedDrivingHistory);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DrivingHistory> deleteDrivingHistory(@PathVariable Long id) {
        drivingHistoryService.deleteDrivingHistory(id);
        return ResponseEntity.ok().build();
    }

}
