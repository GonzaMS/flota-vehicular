package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.*;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.services.ICarService;
import com.proyecto.flotavehicular_webapp.services.Impl.IHeaderDetailsServiceImpl;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    @Value("${page.size}")
    private int defaultPageSize;

    private final ICarService carService;
    private final IHeaderDetailsServiceImpl headerDetailsService;

    public CarController(ICarService carService, IHeaderDetailsServiceImpl headerDetailsService) {
        this.carService = carService;
        this.headerDetailsService = headerDetailsService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<CarDTO>> getAllCars(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<CarDTO> carPageResponse = carService.getAll(pageNumber, effectivePageSize);

        if (carPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(carPageResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Long id) {
        CarDTO car = carService.getById(id);

        if (car == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(car);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<CarWithDetailsDTO> getCarDetailsById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize
    ) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        CarWithDetailsDTO carWithDetails = headerDetailsService.getCarWithDetails(id, pageNumber, effectivePageSize);

        if (carWithDetails == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(carWithDetails);
    }

    @PostMapping()
    public ResponseEntity<Car> saveCar(@Valid @RequestBody CarDTO carDTO) {
        Car newCar = carService.save(carDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCar);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarDTO> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarDTO carDTO) {

        carService.update(id, carDTO);

        CarDTO updateCar = carService.getById(id);

        return ResponseEntity.ok().body(updateCar);
    }

    @PutMapping("/{id}/incidents")
    public ResponseEntity<Void> updateCarIncidents(
            @PathVariable Long id,
            @RequestBody List<CarIncidentsDTO> incidentsDTOs) {

        headerDetailsService.updateIncidents(id, incidentsDTOs);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/kilometers")
    public ResponseEntity<Void> updateCarKilometers(
            @PathVariable Long id,
            @RequestBody List<KilometersDTO> kilometersDTOs) {

        headerDetailsService.updateKilometers(id, kilometersDTOs);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/maintenance")
    public ResponseEntity<Void> updateCarMaintenance(
            @PathVariable Long id,
            @RequestBody List<MaintenanceHistoryDTO> maintenanceDTOs) {

        headerDetailsService.updateMaintenance(id, maintenanceDTOs);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCar(@PathVariable Long id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<CarDTO> deactivateCar(@PathVariable Long id) {
        carService.deactivate(id);
        CarDTO updatedCar = carService.getById(id);
        return ResponseEntity.ok(updatedCar);
    }

    // Filters
    @GetMapping("/state/{state}")
    public ResponseEntity<PageResponse<CarDTO>> getCarByState(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @PathVariable String state) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<CarDTO> carPageResponse = carService.getByState(state, pageNumber, effectivePageSize);

        if (carPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(carPageResponse);
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<PageResponse<CarDTO>> getCarByBrand(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @PathVariable String brand) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<CarDTO> carPageResponse = carService.getByBrand(brand, pageNumber, effectivePageSize);

        if (carPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(carPageResponse);
    }

    @GetMapping("/model/{model}")
    public ResponseEntity<PageResponse<CarDTO>> getCarByModel(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @PathVariable String model) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<CarDTO> carPageResponse = carService.getByModel(model, pageNumber, effectivePageSize);

        if (carPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(carPageResponse);
    }

    @GetMapping("/licensePlate/{licensePlate}")
    public ResponseEntity<PageResponse<CarDTO>> getCarByLicensePlate(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @PathVariable String licensePlate) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<CarDTO> carPageResponse = carService.getByLicensePlate(licensePlate, pageNumber, effectivePageSize);

        if (carPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(carPageResponse);
    }
}
