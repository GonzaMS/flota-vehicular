package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.CarDTO;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.services.ICarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    private final ICarService carService;

    public CarController(ICarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<CarDTO>> getAllCars(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        PageResponse<CarDTO> carPageResponse = carService.getAllCars(pageNumber, pageSize);

        if (carPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(carPageResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Long id) {
        CarDTO car = carService.getCarById(id);
        return ResponseEntity.ok(car);
    }

    @PostMapping()
    public ResponseEntity<Car> saveCar(@Valid @RequestBody CarDTO carDTO) {
        Car newCar = carService.saveCar(carDTO);

        if(newCar == null){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(newCar);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable Long id, @Valid @RequestBody CarDTO carDTO) {
        carService.updateCar(id, carDTO);
        return ResponseEntity.ok().body(carDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Car> deleteCar(@PathVariable Long id){
        carService.deleteCar(id);
        return ResponseEntity.ok().build();
    }

    // Filters
    @GetMapping("/state")
    public ResponseEntity<PageResponse<CarDTO>> getCarByState(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String state) {

        PageResponse<CarDTO> carPageResponse = carService.getCarByState(state, pageSize, pageNumber);

        if (carPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(carPageResponse);
    }

    @GetMapping("/brand")
    public ResponseEntity<PageResponse<CarDTO>> getCarByBrand(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String brand) {

        PageResponse<CarDTO> carPageResponse = carService.getCarByBrand(brand, pageSize, pageNumber);

        if (carPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(carPageResponse);
    }

    @GetMapping("/model")
    public ResponseEntity<PageResponse<CarDTO>> getCarByModel(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String model) {

        PageResponse<CarDTO> carPageResponse = carService.getCarByModel(model, pageNumber, pageSize);

        if (carPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(carPageResponse);
    }

    @GetMapping("/licensePlate")
    public ResponseEntity<PageResponse<CarDTO>> getCarByLicensePlate(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String licensePlate) {

        PageResponse<CarDTO> carPageResponse = carService.getCarByLicensePlate(licensePlate, pageNumber, pageSize);

        if (carPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(carPageResponse);
    }

    @GetMapping("/simulate-error")
    public ResponseEntity<String> simulateInternalError() {
        // Forzar una excepción interna para probar el manejo de errores 500
        throw new InternalError("Simulated internal server error");
    }

}
