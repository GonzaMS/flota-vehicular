package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.KilometersDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.models.Kilometers;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.repositories.IKilometersRepository;
import com.proyecto.flotavehicular_webapp.services.IKilometersService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IKilometersImpl implements IKilometersService {

    private final IKilometersRepository kilometersRepository;
    private final ICarRepository carRepository;

    private static final String KILOMETERS_NOT_FOUND = "Kilometers not found";

    private static final Logger logger = LoggerFactory.getLogger(IKilometersImpl.class);

    public IKilometersImpl(IKilometersRepository kilometersRepository, ICarRepository carRepository) {
        this.kilometersRepository = kilometersRepository;
        this.carRepository = carRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<KilometersDTO> getAllKilometers(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Kilometers> kilometersPage = kilometersRepository.findAll(pageable);

            List<KilometersDTO> kilometersDTOList = kilometersPage.stream()
                    .map(this::mapToDTO)
                    .toList();

            return PageResponse.of(
                    kilometersDTOList,
                    kilometersPage.getNumber(),
                    kilometersPage.getSize(),
                    kilometersPage.getTotalElements(),
                    kilometersPage.getTotalPages(),
                    kilometersPage.isLast());

        } catch (Exception e) {
            logger.error("Error getting all kilometers: {}", e.getMessage());
            throw new NotFoundException("Error getting all kilometers");
        }

    }

    @Override
    @Transactional(readOnly = true)
    public KilometersDTO getKilometersById(Long id) {
        Kilometers kilometers = kilometersRepository.findById(id).orElseThrow(() -> new NotFoundException(KILOMETERS_NOT_FOUND));
        return mapToDTO(kilometers);
    }

    @Override
    @Transactional
    public Kilometers saveKilometers(KilometersDTO kilometersDTO) {
        try {
            Car car = carRepository.findById(kilometersDTO.getCarId()).orElseThrow(() -> new NotFoundException("Car not found"));

            Kilometers kilometers = mapToEntity(kilometersDTO);
            kilometers.setCar(car);

            return kilometersRepository.save(kilometers);

        } catch (Exception e) {
            logger.error("Error saving kilometers: {}", e.getMessage());
            throw new NotFoundException("Error saving kilometers");
        }

    }

    @Override
    @Transactional
    public void updateKilometers(Long id, KilometersDTO kilometersDTO) {
        try {
            Kilometers kilometers = kilometersRepository.findById(id).orElseThrow(() -> new NotFoundException(KILOMETERS_NOT_FOUND));
            kilometers.setActualKm(kilometersDTO.getActualKm());
            kilometers.setUpdateKmDate(kilometersDTO.getUpdateKmDate());
            kilometersRepository.save(kilometers);

        } catch (Exception e) {
            logger.error("Error updating kilometers: {}", e.getMessage());
            throw new NotFoundException("Error updating kilometers");
        }

    }

    @Override
    @Transactional
    public void deleteKilometers(Long id) {
        Kilometers kilometers = kilometersRepository.findById(id).orElseThrow(() -> new NotFoundException(KILOMETERS_NOT_FOUND));
        kilometersRepository.delete(kilometers);
    }

    // Filters
    @Override
    @Transactional(readOnly = true)
    public PageResponse<KilometersDTO> getKilometersByCarId(Long carId, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Kilometers> kilometersPage = kilometersRepository.findByCar_CarId(carId, pageable);

            if (kilometersPage.isEmpty()) {
                throw new NotFoundException(KILOMETERS_NOT_FOUND + " for car with id: " + carId);
            }

            List<KilometersDTO> kilometersDTOList = kilometersPage.stream()
                    .map(this::mapToDTO)
                    .toList();

            return PageResponse.of(
                    kilometersDTOList,
                    kilometersPage.getNumber(),
                    kilometersPage.getSize(),
                    kilometersPage.getTotalElements(),
                    kilometersPage.getTotalPages(),
                    kilometersPage.isLast());

        } catch (Exception e) {
            logger.error("Error getting all kilometers: {}", e.getMessage());
            throw new NotFoundException("Error getting all kilometers");
        }
    }

    // Mappers
    // Map Entity to DTO
    private KilometersDTO mapToDTO(Kilometers kilometers) {
        return KilometersDTO.builder()
                .kilometersId(kilometers.getKilometersId())
                .updateKmDate(kilometers.getUpdateKmDate())
                .actualKm(kilometers.getActualKm())
                .carId(kilometers.getCar().getCarId())
                .build();
    }

    // Map DTO to Entity
    private Kilometers mapToEntity(KilometersDTO kilometersDTO) {
        return Kilometers.builder()
                .kilometersId(kilometersDTO.getKilometersId())
                .updateKmDate(kilometersDTO.getUpdateKmDate())
                .actualKm(kilometersDTO.getActualKm())
                .build();
    }
}