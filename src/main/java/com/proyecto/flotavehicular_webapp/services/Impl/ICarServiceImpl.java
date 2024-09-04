package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.CarDTO;
import com.proyecto.flotavehicular_webapp.dto.Pageables.CarPageResponse;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.services.ICarService;
import com.proyecto.flotavehicular_webapp.utils.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ICarServiceImpl implements ICarService {

    private final ICarRepository carRepository;

    private static final String NOTFOUND = "Car not found";

    public ICarServiceImpl(ICarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<CarDTO> getAllCars() {
        List<Car> cars = carRepository.findAll();
        return cars.stream().map(this::mapToDTO).toList();
    }

    @Override
    public CarDTO getCarById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(car);
    }

    @Override
    public Car saveCar(CarDTO carDTO) {
        Car car = mapToEntity(carDTO);
        return carRepository.save(car);
    }

    @Override
    public void updateCar(Long id, CarDTO carDTO) {
        Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        car.setCarBrand(carDTO.getCarBrand());
        car.setCarModel(carDTO.getCarModel());
        car.setCarLicensePlate(carDTO.getCarLicensePlate());
        car.setCarFabricationYear(carDTO.getCarFabricationYear());
        car.setCarState(carDTO.getCarState());
        carRepository.save(car);
    }

    @Override
    public void deleteCar(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        carRepository.delete(car);
    }

    @Override
    public CarPageResponse getAllPagesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PaginationUtil.createPageable(pageNumber, pageSize);
        return getCarPageResponse(pageable);
    }

    @Override
    public CarPageResponse getAllPagesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Pageable pageable = PaginationUtil.createPageable(pageNumber, pageSize, sortBy, dir);
        return getCarPageResponse(pageable);
    }

    private CarPageResponse getCarPageResponse(Pageable pageable) {
        Page<Car> carPage = carRepository.findAll(pageable);

        List<CarDTO> cars = new ArrayList<>();

        carPage.getContent().forEach(car -> cars.add(mapToDTO(car)));

        return new CarPageResponse(cars, carPage.getNumber(), carPage.getSize(), (int) carPage.getTotalElements(), carPage.getTotalPages(), carPage.isLast());
    }

    // Mapping DTO to Car Model
    private Car mapToEntity(CarDTO carDTO) {
        return Car.builder()
                .carId(carDTO.getCarId())
                .carBrand(carDTO.getCarBrand())
                .carModel(carDTO.getCarModel())
                .carLicensePlate(carDTO.getCarLicensePlate())
                .carFabricationYear(carDTO.getCarFabricationYear())
                .carState(carDTO.getCarState())
                .build();
    }

    // Mapping the Car object to DTO
    private CarDTO mapToDTO(Car car) {
        return CarDTO.builder()
                .carId(car.getCarId())
                .carBrand(car.getCarBrand())
                .carModel(car.getCarModel())
                .carLicensePlate(car.getCarLicensePlate())
                .carFabricationYear(car.getCarFabricationYear())
                .carState(car.getCarState())
                .build();
    }

}
