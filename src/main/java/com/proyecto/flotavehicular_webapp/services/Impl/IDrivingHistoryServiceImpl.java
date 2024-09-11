package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.DrivingHistoryDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.DrivingHistory;
import com.proyecto.flotavehicular_webapp.repositories.IDrivingHistoryRepository;
import com.proyecto.flotavehicular_webapp.services.IDrivingHistoryService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IDrivingHistoryServiceImpl implements IDrivingHistoryService {

    private final IDrivingHistoryRepository drivingHistoryRepository;

    private static final String NOTFOUND = "Driving history not found";

    public IDrivingHistoryServiceImpl(IDrivingHistoryRepository drivingHistoryRepository) {
        this.drivingHistoryRepository = drivingHistoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DrivingHistoryDTO> getAllDrivingHistories(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<DrivingHistory> drivingHistoryPage = drivingHistoryRepository.findAll(pageable);

        List<DrivingHistoryDTO> drivingHistoryDTOList = drivingHistoryPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                drivingHistoryDTOList,
                drivingHistoryPage.getNumber(),
                drivingHistoryPage.getSize(),
                drivingHistoryPage.getTotalElements(),
                drivingHistoryPage.getTotalPages(),
                drivingHistoryPage.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public DrivingHistoryDTO getDrivingHistoryById(Long id) {
        DrivingHistory drivingHistory = drivingHistoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(drivingHistory);
    }

    @Override
    @Transactional
    public void saveDrivingHistory(DrivingHistoryDTO drivingHistoryDTO) {
        DrivingHistory drivingHistory = mapToEntity(drivingHistoryDTO);
        drivingHistoryRepository.save(drivingHistory);
    }

    @Override
    @Transactional
    public void updateDrivingHistory(Long id, DrivingHistoryDTO drivingHistoryDTO) {
        DrivingHistory drivingHistory = drivingHistoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOTFOUND));

        drivingHistory.setDrivingDate(drivingHistoryDTO.getDrivingDate());
        drivingHistory.setKmDriven(drivingHistoryDTO.getKmDriven());

        // Asumiendo que Driver y Car están bien gestionados en el DTO
        // podrías agregar lógica para obtener entidades de Driver y Car aquí
        // y actualizar las referencias si es necesario

        drivingHistoryRepository.save(drivingHistory);
    }

    @Override
    @Transactional
    public void deleteDrivingHistory(Long id) {
        DrivingHistory drivingHistory = drivingHistoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOTFOUND));
        drivingHistoryRepository.delete(drivingHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DrivingHistoryDTO> getDrivingHistoryByDriverId(Long driverId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<DrivingHistory> drivingHistoryPage = drivingHistoryRepository.findByDriverDriverId(driverId, pageable);

        if (drivingHistoryPage.isEmpty()) {
            throw new NotFoundException("Driver ID not found: " + driverId);
        }

        List<DrivingHistoryDTO> drivingHistoryDTOList = drivingHistoryPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                drivingHistoryDTOList,
                drivingHistoryPage.getNumber(),
                drivingHistoryPage.getSize(),
                drivingHistoryPage.getTotalElements(),
                drivingHistoryPage.getTotalPages(),
                drivingHistoryPage.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DrivingHistoryDTO> getDrivingHistoryByCarId(Long carId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<DrivingHistory> drivingHistoryPage = drivingHistoryRepository.findByCarCarId(carId, pageable);

        if (drivingHistoryPage.isEmpty()) {
            throw new NotFoundException("Car ID not found: " + carId);
        }

        List<DrivingHistoryDTO> drivingHistoryDTOList = drivingHistoryPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                drivingHistoryDTOList,
                drivingHistoryPage.getNumber(),
                drivingHistoryPage.getSize(),
                drivingHistoryPage.getTotalElements(),
                drivingHistoryPage.getTotalPages(),
                drivingHistoryPage.isLast());
    }

    // Mappers
    private DrivingHistory mapToEntity(DrivingHistoryDTO drivingHistoryDTO) {
        return DrivingHistory.builder()
                .drivingHistoryId(drivingHistoryDTO.getDrivingHistoryId())
                .drivingDate(drivingHistoryDTO.getDrivingDate())
                .kmDriven(drivingHistoryDTO.getKmDriven())
                .build();
    }

    private DrivingHistoryDTO mapToDTO(DrivingHistory drivingHistory) {
        return DrivingHistoryDTO.builder()
                .drivingHistoryId(drivingHistory.getDrivingHistoryId())
                .driverId(drivingHistory.getDriver().getDriverId())
                .carId(drivingHistory.getCar().getCarId())
                .drivingDate(drivingHistory.getDrivingDate())
                .kmDriven(drivingHistory.getKmDriven())
                .build();
    }
}
