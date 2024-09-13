package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.TravelOrderDTO;
import com.proyecto.flotavehicular_webapp.services.ITravelOrderService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/travel-orders")
public class TravelOrderController {

    @Autowired
    private ITravelOrderService travelOrderService;

    @GetMapping
    public ResponseEntity<PageResponse<TravelOrderDTO>> getAllTravelOrders(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<TravelOrderDTO> ordersPage = travelOrderService.getAllTravelOrders(pageNumber, pageSize);
        return ResponseEntity.ok(ordersPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelOrderDTO> getTravelOrderById(@PathVariable Long id) {
        TravelOrderDTO travelOrder = travelOrderService.getTravelOrderById(id);
        return ResponseEntity.ok(travelOrder);
    }

    @PostMapping
    public ResponseEntity<TravelOrderDTO> createTravelOrder(@Valid @RequestBody TravelOrderDTO travelOrderDTO) {
        TravelOrderDTO newOrder = travelOrderService.createTravelOrder(travelOrderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelOrderDTO> updateTravelOrder(@PathVariable Long id, @Valid @RequestBody TravelOrderDTO travelOrderDTO) {
        travelOrderService.updateTravelOrder(id, travelOrderDTO);
        return ResponseEntity.ok(travelOrderDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTravelOrder(@PathVariable Long id) {
        travelOrderService.deleteTravelOrder(id);
        return ResponseEntity.noContent().build();
    }
}
