package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.driver.DriverDTO;
import com.proyecto.flotavehicular_webapp.models.Driver.Driver;
import com.proyecto.flotavehicular_webapp.services.IDriverService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

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


        return ResponseEntity.ok(driverPageResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverDTO> getDriverById(@PathVariable Long id) {
        DriverDTO driver = driverService.getDriverById(id);

        if (driver ==null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(driver);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Driver> saveDriver(@Valid @RequestBody DriverDTO driverDTO) {
        Driver newDriver = driverService.saveDriver(driverDTO);

        if (newDriver == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newDriver);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverDTO> updateDriver(
            @PathVariable Long id,
            @Valid @RequestBody DriverDTO driverDTO) {

        driverService.updateDriver(id, driverDTO);

        DriverDTO driver = driverService.getDriverById(id);
        return ResponseEntity.ok().body(driver);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok().build();
    }

    // Filters
    @GetMapping("/state/{state}")
    public ResponseEntity<PageResponse<DriverDTO>> getDriverByState(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @PathVariable String state) {

        PageResponse<DriverDTO> driverPageResponse = driverService.getDriverByState(state, pageNumber, pageSize);

        if (driverPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(driverPageResponse);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PageResponse<DriverDTO>> getDriverByName(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @PathVariable String name) {

        PageResponse<DriverDTO> driverPageResponse = driverService.getDriverByName(name, pageNumber, pageSize);

        if (driverPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(driverPageResponse);
    }

    @GetMapping("/license/{license}")
    public ResponseEntity<PageResponse<DriverDTO>> getDriverByLicense(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @PathVariable String license) {

        PageResponse<DriverDTO> driverPageResponse = driverService.getDriverByLicense(license, pageNumber, pageSize);

        if (driverPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(driverPageResponse);
    }
}
