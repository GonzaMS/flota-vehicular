package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.CarDTO;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.models.Car;


public interface ICarService {

    PageResponse<CarDTO> getAll(int pageNumber, int pageSize);

    CarDTO getById(Long id);

    Car save(CarDTO carDTO);

    CarDTO update(Long id, CarDTO carDTO);

    void delete(Long id);

    void deactivate(Long id);

    // Filters
    PageResponse<CarDTO> getByState(String state, int pageNumber, int pageSize);

    PageResponse<CarDTO> getByBrand(String brand, int pageNumber, int pageSize);

    PageResponse<CarDTO> getByModel(String model, int pageNumber, int pageSize);

    PageResponse<CarDTO> getByLicensePlate(String licensePlate, int pageNumber, int pageSize);
}
