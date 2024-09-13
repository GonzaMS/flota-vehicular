package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.CarIncidentsDTO;
import com.proyecto.flotavehicular_webapp.models.CarIncidents;
import com.proyecto.flotavehicular_webapp.services.ICarIncidentsService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/incidents")
public class CarIncidentsController {

    @Value("${page.size}")
    private int defaultPageSize;

    private final ICarIncidentsService carIncidentsService;

    public CarIncidentsController(ICarIncidentsService carIncidentsService) {
        this.carIncidentsService = carIncidentsService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<CarIncidentsDTO>> getAllIncidents(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<CarIncidentsDTO> incidents = carIncidentsService.getAll(pageNumber, effectivePageSize);

        if (incidents.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarIncidentsDTO> getIncidentById(@PathVariable Long id) {
        CarIncidentsDTO incident = carIncidentsService.getById(id);
        return ResponseEntity.ok(incident);
    }

    @PostMapping
    public ResponseEntity<CarIncidents> saveIncident(@Valid @RequestBody CarIncidentsDTO carIncidentsDTO) {
        CarIncidents newIncident = carIncidentsService.save(carIncidentsDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newIncident);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarIncidentsDTO> updateIncident(
            @PathVariable Long id,
            @Valid @RequestBody CarIncidentsDTO carIncidentsDTO) {

        carIncidentsService.update(id, carIncidentsDTO);

        CarIncidentsDTO carIncidentsDTO1 = carIncidentsService.getById(id);

        return ResponseEntity.ok().body(carIncidentsDTO1);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CarIncidents> deleteMaintenance(@PathVariable Long id) {
        carIncidentsService.delete(id);
        return ResponseEntity.ok().build();
    }

    // Filters
    @GetMapping("/car/{carId}")
    public ResponseEntity<PageResponse<CarIncidentsDTO>> getIncidentsByCarId(
            @PathVariable Long carId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<CarIncidentsDTO> carIncidentsDTOPageResponse = carIncidentsService.getByCarId(carId, pageNumber, effectivePageSize);

        if (carIncidentsDTOPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(carIncidentsDTOPageResponse);
    }
}
