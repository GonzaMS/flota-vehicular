package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.travel.AssignedOrderDTO;
import com.proyecto.flotavehicular_webapp.models.Travel.AssignedOrder;
import com.proyecto.flotavehicular_webapp.services.IAssignedOrderService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/assigned-orders")
public class AssignedOrderController {

    private final IAssignedOrderService assignedOrderService;

    @Autowired
    public AssignedOrderController(IAssignedOrderService assignedOrderService) {
        this.assignedOrderService = assignedOrderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignedOrderDTO> getAssignedOrderById(@PathVariable Long id) {
        AssignedOrderDTO assignedOrderDTO = assignedOrderService.getAssignedOrderById(id);
        return ResponseEntity.ok(assignedOrderDTO);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssignedOrder> saveAssignedOrder(
            @RequestBody AssignedOrderDTO assignedOrderDTO,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        AssignedOrder savedOrder = assignedOrderService.saveAssignedOrder(assignedOrderDTO, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssignedOrderDTO> updateAssignedOrder(
            @PathVariable Long id,
            @RequestBody AssignedOrderDTO assignedOrderDTO,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        AssignedOrderDTO updatedOrder = assignedOrderService.updateAssignedOrder(id, assignedOrderDTO, token);
        return ResponseEntity.ok(updatedOrder);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAssignedOrder(@PathVariable Long id) {
        assignedOrderService.deleteAssignedOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<AssignedOrderDTO>> getAllAssignedOrders(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<AssignedOrderDTO> pageResponse = assignedOrderService.getAllAssignedOrders(pageNumber, pageSize);
        return ResponseEntity.ok(pageResponse);
    }

    // Manejador de excepciones para ServiceException
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<String> handleServiceException(ServiceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
