package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.DrivingHistoryDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.models.Driver;
import com.proyecto.flotavehicular_webapp.models.DrivingHistory;
import com.proyecto.flotavehicular_webapp.repositories.IDriverRepository;
import com.proyecto.flotavehicular_webapp.repositories.IDrivingHistoryRepository;
import com.proyecto.flotavehicular_webapp.services.ExternalApiService;
import com.proyecto.flotavehicular_webapp.services.IDrivingHistoryService;
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
public class IDrivingHistoryServiceImpl implements IDrivingHistoryService {

    private final IDrivingHistoryRepository drivingHistoryRepository;

    private static final String NOTFOUND = "Driving history not found";

    private static final Logger logger = LoggerFactory.getLogger(IDrivingHistoryServiceImpl.class);
    private final IDriverRepository driverRepository;
    private final ExternalApiService externalApiService;


    public IDrivingHistoryServiceImpl(IDrivingHistoryRepository drivingHistoryRepository, IDriverRepository driverRepository, ExternalApiService externalApiService ) {
        this.drivingHistoryRepository = drivingHistoryRepository;
        this.driverRepository = driverRepository;
        this.externalApiService = externalApiService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DrivingHistoryDTO> getAllDrivingHistories(int pageNumber, int pageSize) {
        try {
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
                    drivingHistoryPage.isLast()
            );
        } catch (Exception e) {
            logger.error("Error getting all driving histories: {}", e.getMessage());
            throw new NotFoundException("Error getting all driving histories");
        }
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
    public DrivingHistory saveDrivingHistory(DrivingHistoryDTO drivingHistoryDTO) {
        try {
            Driver driver = driverRepository.findById(drivingHistoryDTO.getCarId()).orElseThrow(() -> new NotFoundException("Driver not found"));
            Car car = externalApiService.callExternalApi(drivingHistoryDTO.getCarId());
            if (car == null){
                throw new NotFoundException("Car not found");
            }
            DrivingHistory drivingHistory = mapToEntity(drivingHistoryDTO);
            drivingHistory.setDriver(driver);
            drivingHistory.setCar(car);

            return drivingHistoryRepository.save(drivingHistory);

        } catch (Exception e) {
            logger.error("Error saving driving history: {}", e.getMessage());
            throw new NotFoundException("Error saving driving history");
        }
    }


    @Override
    @Transactional
    public void updateDrivingHistory(Long id, DrivingHistoryDTO drivingHistoryDTO) {
        try {
            DrivingHistory drivingHistory = drivingHistoryRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(NOTFOUND));

            drivingHistory.setDrivingDate(drivingHistoryDTO.getDrivingDate());
            drivingHistory.setKmDriven(drivingHistoryDTO.getKmDriven());

            drivingHistoryRepository.save(drivingHistory);

        } catch (Exception e) {
            logger.error("Error updating driving history: {}", e.getMessage());
            throw new NotFoundException("Error saving driving history");
        }
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
        try {
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

        } catch (Exception e) {
            logger.error("Error getting maintenance by car id: {}", e.getMessage());
            throw new NotFoundException("Error getting maintenance by car id");
        }
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
    private DrivingHistoryDTO mapToDTO(DrivingHistory drivingHistory) {
        return DrivingHistoryDTO.builder()
                .drivingHistoryId(drivingHistory.getDrivingHistoryId())
                .drivingDate(drivingHistory.getDrivingDate())
                .kmDriven(drivingHistory.getKmDriven())
                .driverId(drivingHistory.getDriver().getDriverId())
                .carId(drivingHistory.getCar().getCarId())
                .build();
    }

    private DrivingHistory mapToEntity(DrivingHistoryDTO drivingHistoryDTO) {
        return DrivingHistory.builder()
                .drivingHistoryId(drivingHistoryDTO.getDrivingHistoryId())
                .drivingDate(drivingHistoryDTO.getDrivingDate())
                .kmDriven(drivingHistoryDTO.getKmDriven())
                .build();
    }
}
