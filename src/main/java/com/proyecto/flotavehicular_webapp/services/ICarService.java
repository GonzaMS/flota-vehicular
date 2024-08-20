package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.CarDTO;
import com.proyecto.flotavehicular_webapp.dto.CarPageResponse;
import com.proyecto.flotavehicular_webapp.models.Car;

import java.util.List;

public interface ICarService {

    List<CarDTO> getAllCars();

    CarDTO getCarById(Long id);

    Car saveCar(CarDTO carDTO);

    void updateCar(Long id, CarDTO carDTO);

    void deleteCar(Long id);

    CarPageResponse getAllPagesWithPagination(Integer pageNumber, Integer pageSize);

    CarPageResponse getAllPagesWithPaginationAndSorting(Integer pageNumber, Integer pageSize,
                                                        String sortBy,
                                                        String dir);



    // Filters
    //List<CarDTO> getCarsByBrand(String brand);

    //List<CarDTO> getCarsByModel(String model);

    //List<CarDTO> getCarsByFabricationYear(String fabricationYear);

    List<CarDTO> getCarsByState(String state);
}
