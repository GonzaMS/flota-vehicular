package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.driver.DriverIncidentsDTO;
import com.proyecto.flotavehicular_webapp.models.Driver.DriverIncidents;
import com.proyecto.flotavehicular_webapp.services.IDriverIncidentsService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/v1/incidents")
public class DriverIncidentsController {

    private final IDriverIncidentsService incidentRecordService;

    public DriverIncidentsController(IDriverIncidentsService incidentRecordService) {
        this.incidentRecordService = incidentRecordService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverIncidents> createIncidentRecord(@Valid @RequestBody DriverIncidentsDTO driverIncidentsDTO) {
        DriverIncidents createdIncidentRecord = incidentRecordService.saveIncidentRecord(driverIncidentsDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIncidentRecord);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverIncidentsDTO> getIncidentRecordById(@PathVariable("id") Long id) {
        DriverIncidentsDTO driverIncidentsDTO = incidentRecordService.getIncidentRecordById(id);
        return ResponseEntity.ok(driverIncidentsDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverIncidentsDTO> updateIncidentRecord(@PathVariable("id") Long id, @Valid @RequestBody DriverIncidentsDTO driverIncidentsDTO) {
        incidentRecordService.updateIncidentRecord(id, driverIncidentsDTO);
        DriverIncidentsDTO updatedIncidentRecord = incidentRecordService.getIncidentRecordById(id);
        return ResponseEntity.ok(updatedIncidentRecord);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteIncidentRecord(@PathVariable("id") Long id) {
        incidentRecordService.deleteIncidentRecord(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<DriverIncidentsDTO>> getAllIncidentRecords(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<DriverIncidentsDTO> pageResponse = incidentRecordService.getAllIncidentRecords(pageNumber, pageSize);
        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<PageResponse<DriverIncidentsDTO>> getIncidentRecordsByDriver(
            @PathVariable Long driverId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<DriverIncidentsDTO> pageResponse = incidentRecordService.getIncidentRecordsByDriver(driverId, pageNumber, pageSize);
        return ResponseEntity.ok(pageResponse);
    }
}
