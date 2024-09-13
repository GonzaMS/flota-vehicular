package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.AssignedOrderDTO;
import com.proyecto.flotavehicular_webapp.models.AssignedOrder;
import com.proyecto.flotavehicular_webapp.services.IAssignedOrderService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AssignedOrder> createAssignedOrder(@RequestBody AssignedOrderDTO assignedOrderDTO) {
        AssignedOrder assignedOrder = assignedOrderService.saveAssignedOrder(assignedOrderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignedOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssignedOrderDTO> updateAssignedOrder(
            @PathVariable Long id,
            @Valid @RequestBody AssignedOrderDTO orderDTO) {

        assignedOrderService.updateAssignedOrder(id, orderDTO);

        AssignedOrderDTO updateOrder = assignedOrderService.getAssignedOrderById(id);

        return ResponseEntity.ok().body(updateOrder);
    }

    @DeleteMapping("/{id}")
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
}
