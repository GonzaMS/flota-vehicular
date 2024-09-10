package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.CarIncidentsDTO;
import com.proyecto.flotavehicular_webapp.models.CarIncidents;
import com.proyecto.flotavehicular_webapp.services.ICarIncidentsService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/incidents")
public class CarIncidentsController {
    private final ICarIncidentsService carIncidentsService;

    public CarIncidentsController(ICarIncidentsService carIncidentsService){
        this.carIncidentsService = carIncidentsService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<CarIncidentsDTO>> getAllIncidents(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize){

            PageResponse<CarIncidentsDTO> incidents = carIncidentsService.getAllIncidents(pageNumber, pageSize);

            if (incidents.items().isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(incidents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarIncidentsDTO> getIncidentById(@PathVariable  Long id){
        CarIncidentsDTO incident = carIncidentsService.getIncidentById(id);
        return ResponseEntity.ok(incident);
    }

    @PostMapping
    public ResponseEntity<CarIncidents> saveIncident(@Valid @RequestBody CarIncidentsDTO carIncidentsDTO){
        CarIncidents newIncident = carIncidentsService.saveIncident(carIncidentsDTO);

        if(newIncident == null){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(newIncident);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarIncidentsDTO> updateIncident(
            @PathVariable Long id,
            @Valid @RequestBody CarIncidentsDTO carIncidentsDTO){

        carIncidentsService.updateIncident(id, carIncidentsDTO);

        CarIncidentsDTO carIncidentsDTO1 = carIncidentsService.getIncidentById(id);

        return ResponseEntity.ok().body(carIncidentsDTO1);
    }

    @DeleteMapping
    public ResponseEntity<CarIncidents> deleteMaintenance(Long id){
        carIncidentsService.deleteIncident(id);
        return ResponseEntity.ok().build();
    }

    // Filters
    @GetMapping("/car/{carId}")
    public ResponseEntity<PageResponse<CarIncidentsDTO>> getIncidentsByCarId(
            @PathVariable Long carId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize){

        PageResponse<CarIncidentsDTO> carIncidentsDTOPageResponse = carIncidentsService.getIncidentsByCarId(carId, pageNumber, pageSize);

        if (carIncidentsDTOPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(carIncidentsDTOPageResponse);
    }
}
