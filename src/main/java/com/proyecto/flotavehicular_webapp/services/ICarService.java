package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.CarDTO;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.models.Car;


public interface ICarService {

    PageResponse<CarDTO> getAllCars(int pageNumber, int pageSize);

    CarDTO getCarById(Long id);

    Car saveCar(CarDTO carDTO);

    void updateCar(Long id, CarDTO carDTO);

    void deleteCar(Long id);

    void deactivateCar(Long id);

    // Filters
    PageResponse<CarDTO> getCarByState(String state, int pageNumber, int pageSize);

    PageResponse<CarDTO> getCarByBrand(String brand, int pageNumber, int pageSize);

    PageResponse<CarDTO> getCarByModel(String model, int pageNumber, int pageSize);

    PageResponse<CarDTO> getCarByLicensePlate(String licensePlate, int pageNumber, int pageSize);
}
