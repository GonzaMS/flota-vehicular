package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.*;
import com.proyecto.flotavehicular_webapp.dto.driver.DriverDTO;
import com.proyecto.flotavehicular_webapp.dto.driver.DriverIncidentsDTO;
import com.proyecto.flotavehicular_webapp.services.Impl.TransactionServiceImpl;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    @Value("${page.size}")
    private int defaultPageSize;

    private final TransactionServiceImpl transactionService;

    public TransactionController(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/read-only/{id}")
    public ResponseEntity<String> readOnly(@PathVariable Long id) {
        transactionService.getIncidentReadOnly(id);
        return ResponseEntity.ok("Incident read successfully with READ_ONLY");
    }

    // REQUIRED
    @PutMapping("/update-direct/{id}")
    public ResponseEntity<String> updateIncidentMandatory(@PathVariable Long id, @RequestBody String description) {
        transactionService.updateIncidentDirect(id, description);
        return ResponseEntity.ok("Incident updated successfully with MANDATORY");
    }

    @PutMapping("/update-indirect/{driverId}/{incidentId}")
    public ResponseEntity<String> updateIncidentIndirect(
            @PathVariable Long driverId,
            @PathVariable Long incidentId,
            @RequestBody DriverAndIncidentDTO driverAndIncidentDTO) {

        transactionService.updateIncidentIndirect(
                driverId,
                incidentId,
                driverAndIncidentDTO.getdriverDTO(),
                driverAndIncidentDTO.getDriverIncidentsDTO());

        return ResponseEntity.ok("Driver and Incident updated successfully.");
    }


    // REQUIRES_NEW
    @PostMapping("/save-driver-direct")
    public ResponseEntity<String> saveDriverDirect(@RequestBody DriverDTO driverDTO) {
        transactionService.saveDriverDirect(driverDTO);
        return ResponseEntity.ok("Driver saved successfully (direct transaction)");
    }

    @PostMapping("/save-incident-indirect")
    public ResponseEntity<String> saveIncidentIndirect(@RequestBody DriverIncidentsDTO incidentDTO) {
        transactionService.saveIncidentIndirect(incidentDTO);
        return ResponseEntity.ok("Incident saved successfully (indirect transaction)");
    }

    // SUPPORTS
    @GetMapping("/read-direct/{id}")
    public ResponseEntity<String> readIncidentSupports(@PathVariable Long id) {
        transactionService.getByIdDirect(id);
        return ResponseEntity.ok("Incident read successfully with SUPPORTS");
    }

    @GetMapping("/read-indirect/{id}")
    public ResponseEntity<String> readIncidentSupportsIndirect(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<DriverIncidentsDTO> driverIncidentsDTOPageResponse = transactionService.geyByIdIndirect(id, pageNumber, effectivePageSize);

        if (driverIncidentsDTOPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok("Incident read successfully with SUPPORTS (indirect)");
    }

    // NOT_SUPPORTED
    @PutMapping("/delete/{id}")
    public ResponseEntity<String> deleteIncidentNotSupported(@PathVariable Long id) {
        transactionService.deleteDriver(id);
        return ResponseEntity.ok("Driver deactivated successfully without transaction (NOT_SUPPORTED)");
    }

    // NEVER
    @PostMapping("/send-notification")
    public ResponseEntity<String> sendNotification(@RequestParam String email, @RequestBody String message) {
        transactionService.sendNotificationNever(email, message);
        return ResponseEntity.ok("Notification sent successfully (NEVER)");
    }
}