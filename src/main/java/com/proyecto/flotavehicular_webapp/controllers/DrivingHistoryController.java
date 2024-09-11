package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.DrivingHistoryDTO;
import com.proyecto.flotavehicular_webapp.services.IDrivingHistoryService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
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

    // Obtener todos los registros de historial de conducción con paginación
    @GetMapping
    public ResponseEntity<PageResponse<DrivingHistoryDTO>> getAllDrivingHistories(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<DrivingHistoryDTO> drivingHistories = drivingHistoryService.getAllDrivingHistories(pageNumber, pageSize);
        return ResponseEntity.ok(drivingHistories);
    }

    // Obtener un registro de historial de conducción por ID
    @GetMapping("/{id}")
    public ResponseEntity<DrivingHistoryDTO> getDrivingHistoryById(@PathVariable("id") Long id) {
        DrivingHistoryDTO drivingHistory = drivingHistoryService.getDrivingHistoryById(id);
        return ResponseEntity.ok(drivingHistory);
    }

    // Guardar un nuevo registro de historial de conducción
    @PostMapping
    public ResponseEntity<Void> saveDrivingHistory(@Valid @RequestBody DrivingHistoryDTO drivingHistoryDTO) {
        drivingHistoryService.saveDrivingHistory(drivingHistoryDTO);
        return ResponseEntity.status(201).build();
    }

    // Actualizar un registro de historial de conducción existente
    @PutMapping("/{id}")
    public ResponseEntity<DrivingHistoryDTO> updateDrivingHistory(@PathVariable("id") Long id, @Valid @RequestBody DrivingHistoryDTO drivingHistoryDTO) {
        drivingHistoryService.updateDrivingHistory(id, drivingHistoryDTO);
        DrivingHistoryDTO updatedDrivingHistory = drivingHistoryService.getDrivingHistoryById(id);
        return ResponseEntity.ok(updatedDrivingHistory);
    }

    // Eliminar un registro de historial de conducción
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrivingHistory(@PathVariable("id") Long id) {
        drivingHistoryService.deleteDrivingHistory(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener registros de historial de conducción por ID del conductor con paginación
    @GetMapping("/by-driver/{driverId}")
    public ResponseEntity<PageResponse<DrivingHistoryDTO>> getDrivingHistoryByDriverId(
            @PathVariable("driverId") Long driverId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<DrivingHistoryDTO> drivingHistories = drivingHistoryService.getDrivingHistoryByDriverId(driverId, pageNumber, pageSize);
        return ResponseEntity.ok(drivingHistories);
    }

    // Obtener registros de historial de conducción por ID del vehículo con paginación
    @GetMapping("/by-car/{carId}")
    public ResponseEntity<PageResponse<DrivingHistoryDTO>> getDrivingHistoryByCarId(
            @PathVariable("carId") Long carId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<DrivingHistoryDTO> drivingHistories = drivingHistoryService.getDrivingHistoryByCarId(carId, pageNumber, pageSize);
        return ResponseEntity.ok(drivingHistories);
    }
}
