package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.DriverDTO;
import com.proyecto.flotavehicular_webapp.models.Driver;
import com.proyecto.flotavehicular_webapp.services.IDriverService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final IDriverService driverService;

    public DriverController(IDriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<DriverDTO>> getAllDrivers(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        PageResponse<DriverDTO> driverPageResponse = driverService.getAllDrivers(pageNumber, pageSize);

        if (driverPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(driverPageResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverDTO> getDriverById(@PathVariable Long id) {
        DriverDTO driver = driverService.getDriverById(id);
        return ResponseEntity.ok(driver);
    }

    @PostMapping
    public ResponseEntity<Driver> saveDriver(@Valid @RequestBody DriverDTO driverDTO) {
        Driver newDriver = driverService.saveDriver(driverDTO);

        if (newDriver == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newDriver);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverDTO> updateDriver(@PathVariable Long id, @Valid @RequestBody DriverDTO driverDTO) {
        driverService.updateDriver(id, driverDTO);
        DriverDTO driver = driverService.getDriverById(id);
        return ResponseEntity.ok(driver);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok().build();
    }

    // Filters
    @GetMapping("/state")
    public ResponseEntity<PageResponse<DriverDTO>> getDriverByState(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String state) {

        PageResponse<DriverDTO> driverPageResponse = driverService.getDriverByState(state, pageNumber, pageSize);

        if (driverPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(driverPageResponse);
    }

    @GetMapping("/name")
    public ResponseEntity<PageResponse<DriverDTO>> getDriverByName(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String name) {

        PageResponse<DriverDTO> driverPageResponse = driverService.getDriverByName(name, pageNumber, pageSize);

        if (driverPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(driverPageResponse);
    }

    @GetMapping("/license")
    public ResponseEntity<PageResponse<DriverDTO>> getDriverByLicense(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String license) {

        PageResponse<DriverDTO> driverPageResponse = driverService.getDriverByLicense(license, pageNumber, pageSize);

        if (driverPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(driverPageResponse);
    }
}
