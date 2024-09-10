package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.CarDTO;
import com.proyecto.flotavehicular_webapp.utils.EnumUtils;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.services.ICarService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ICarServiceImpl implements ICarService {

    private final ICarRepository carRepository;

    private static final String NOTFOUND = "Car not found";

    public ICarServiceImpl(ICarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CarDTO> getAllCars(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Car> carPage = carRepository.findAll(pageable);

        List<CarDTO> carDTOList = carPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                carDTOList,
                carPage.getNumber(),
                carPage.getSize(),
                carPage.getTotalElements(),
                carPage.getTotalPages(),
                carPage.isLast());
    }


    @Override
    @Transactional(readOnly = true)
    public CarDTO getCarById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(car);
    }

    @Override
    @Transactional
    public Car saveCar(CarDTO carDTO) {
        Car car = mapToEntity(carDTO);
        return carRepository.save(car);
    }

    @Override
    @Transactional
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
    @Transactional
    public void deleteCar(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        carRepository.delete(car);
    }

    @Override
    @Transactional
    public void deactivateCar(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        car.setCarState(ESTATES.INACTIVE);
        carRepository.save(car);
    }

    // Filters
    @Override
    @Transactional(readOnly = true)
    public PageResponse<CarDTO> getCarByState(String state, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        if (!EnumUtils.isValidState(state)) {
            throw new NotFoundException("State not found " + state);
        }

        ESTATES carState = ESTATES.valueOf(state);

        Page<Car> carPage = carRepository.findByCarState(carState, pageable);

        List<CarDTO> carDTOList = carPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                carDTOList,
                carPage.getNumber(),
                carPage.getSize(),
                carPage.getTotalElements(),
                carPage.getTotalPages(),
                carPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CarDTO> getCarByBrand(String brand, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Car> carPage = carRepository.findByCarBrand(brand, pageable);

        if (carPage.isEmpty()) {
            throw new NotFoundException("Brand not found " +  brand);
        }

        List<CarDTO> carDTOList = carPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                carDTOList,
                carPage.getNumber(),
                carPage.getSize(),
                carPage.getTotalElements(),
                carPage.getTotalPages(),
                carPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CarDTO> getCarByModel(String model, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Car> carPage = carRepository.findByCarModel(model, pageable);

        if (carPage.isEmpty()) {
            throw new NotFoundException("Model not found " +  model);
        }

        List<CarDTO> carDTOList = carPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                carDTOList,
                carPage.getNumber(),
                carPage.getSize(),
                carPage.getTotalElements(),
                carPage.getTotalPages(),
                carPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CarDTO> getCarByLicensePlate(String licensePlate, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Car> carPage = carRepository.findByCarLicensePlate(licensePlate, pageable);

        if (carPage.isEmpty()) {
            throw new NotFoundException("License Plate not found " +  licensePlate);
        }

        List<CarDTO> carDTOList = carPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                carDTOList,
                carPage.getNumber(),
                carPage.getSize(),
                carPage.getTotalElements(),
                carPage.getTotalPages(),
                carPage.isLast()
        );
    }

    // Mappers
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
