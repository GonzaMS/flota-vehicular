package com.proyecto.flotavehicular_webapp.Controllers;

import com.proyecto.flotavehicular_webapp.DTO.CarDTO;
import com.proyecto.flotavehicular_webapp.Service.CarService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public List<CarDTO> getAllCars() {
        return carService.getAllCars();
    }



}
