package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.TravelObservationDTO;
import com.proyecto.flotavehicular_webapp.services.ITravelObservationService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/travel-observations")
public class TravelObservationController {

    @Autowired
    private ITravelObservationService travelObservationService;

    @GetMapping
    public ResponseEntity<PageResponse<TravelObservationDTO>> getAllTravelObservations(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<TravelObservationDTO> observationsPage = travelObservationService.getAllTravelObservations(pageNumber, pageSize);
        return ResponseEntity.ok(observationsPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelObservationDTO> getTravelObservationById(@PathVariable Long id) {
        TravelObservationDTO travelObservation = travelObservationService.getTravelObservationById(id);
        return ResponseEntity.ok(travelObservation);
    }

    @PostMapping
    public ResponseEntity<TravelObservationDTO> createTravelObservation(@Valid @RequestBody TravelObservationDTO travelObservationDTO) {
        TravelObservationDTO newObservation = travelObservationService.createTravelObservation(travelObservationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newObservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelObservationDTO> updateTravelObservation(@PathVariable Long id, @Valid @RequestBody TravelObservationDTO travelObservationDTO) {
        travelObservationService.updateTravelObservation(id, travelObservationDTO);
        return ResponseEntity.ok(travelObservationDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTravelObservation(@PathVariable Long id) {
        travelObservationService.deleteTravelObservation(id);
        return ResponseEntity.noContent().build();
    }
}
