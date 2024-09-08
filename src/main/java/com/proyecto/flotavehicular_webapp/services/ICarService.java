package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.CarDTO;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.models.Car;


public interface ICarService {

    PageResponse getAllCars(int pageNumber, int pageSize);

    CarDTO getCarById(Long id);

    Car saveCar(CarDTO carDTO);

    void updateCar(Long id, CarDTO carDTO);

    void deleteCar(Long id);

    // Filters
    PageResponse getCarByState(String state, int pageNumber, int pageSize);

    PageResponse getCarByBrand(String brand, int pageNumber, int pageSize);

    PageResponse getCarByModel(String model, int pageNumber, int pageSize);

    PageResponse getCarByLicensePlate(String licensePlate, int pageNumber, int pageSize);
}
