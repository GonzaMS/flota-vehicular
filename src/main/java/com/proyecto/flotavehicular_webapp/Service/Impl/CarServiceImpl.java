package com.proyecto.flotavehicular_webapp.Service.Impl;

import com.proyecto.flotavehicular_webapp.DTO.CarDTO;
import com.proyecto.flotavehicular_webapp.Models.Car;
import com.proyecto.flotavehicular_webapp.Repository.CarRepository;
import com.proyecto.flotavehicular_webapp.Service.CarService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<CarDTO> getAllCars() {
        List<Car> cars = carRepository.findAll();
        return cars.stream().map(this::mapToDTO).collect(Collectors.toList());
    }



    private CarDTO mapToDTO(Car car) {
        return CarDTO.builder()
                .car_id(car.getCar_id())
                .brand(car.getBrand())
                .model(car.getModel())
                .license_plate(car.getLicense_plate())
                .fabrication_year(car.getFabrication_year())
                .state(String.valueOf(car.getState()))
                .build();
    }
}
