package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.DriverDTO;
import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Driver;
import com.proyecto.flotavehicular_webapp.repositories.IDriverRepository;
import com.proyecto.flotavehicular_webapp.services.IDriverService;
import com.proyecto.flotavehicular_webapp.utils.EnumUtils;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IDriverServiceImpl implements IDriverService {

    private final IDriverRepository driverRepository;

    private static final String NOTFOUND = "Driver not found";

    public IDriverServiceImpl(IDriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DriverDTO> getAllDrivers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Driver> driverPage = driverRepository.findAll(pageable);

        List<DriverDTO> driverDTOList = driverPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                driverDTOList,
                driverPage.getNumber(),
                driverPage.getSize(),
                driverPage.getTotalElements(),
                driverPage.getTotalPages(),
                driverPage.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public DriverDTO getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(driver);
    }

    @Override
    @Transactional
    public Driver saveDriver(DriverDTO driverDTO) {
        Driver driver = mapToEntity(driverDTO);
        return driverRepository.save(driver);
    }

    @Override
    @Transactional
    public void updateDriver(Long id, DriverDTO driverDTO) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Driver not found"));

        driver.setDriverName(driverDTO.getDriverName());
        driver.setDriverLicense(driverDTO.getDriverLicense());
        driver.setDriverLicenseExpirationDate(driverDTO.getDriverLicenseExpirationDate());
        driver.setDriverState(driverDTO.getDriverState());

        driverRepository.save(driver);
    }



    @Override
    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOTFOUND));
        driverRepository.delete(driver);
    }

    // Filters
    @Override
    @Transactional(readOnly = true)
    public PageResponse<DriverDTO> getDriverByName(String name, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Driver> driverPage = driverRepository.findByDriverName(name, pageable);

        if (driverPage.isEmpty()) {
            throw new NotFoundException("Name not found: " + name);
        }

        List<DriverDTO> driverDTOList = driverPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                driverDTOList,
                driverPage.getNumber(),
                driverPage.getSize(),
                driverPage.getTotalElements(),
                driverPage.getTotalPages(),
                driverPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DriverDTO> getDriverByLicense(String license, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Driver> driverPage = driverRepository.findByDriverLicense(license, pageable);

        if (driverPage.isEmpty()) {
            throw new NotFoundException("License not found: " + license);
        }

        List<DriverDTO> driverDTOList = driverPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                driverDTOList,
                driverPage.getNumber(),
                driverPage.getSize(),
                driverPage.getTotalElements(),
                driverPage.getTotalPages(),
                driverPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DriverDTO> getDriverByState(String state, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        if(!EnumUtils.isValidState(state)) {
            throw new NotFoundException("State not found: " + state);
        }
        ESTATES driverState = ESTATES.valueOf(state);
        Page<Driver> driverPage = driverRepository.findByDriverState(driverState, pageable);

        if (driverPage.isEmpty()) {
            throw new NotFoundException("State not found: " + state);
        }

        List<DriverDTO> driverDTOList = driverPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                driverDTOList,
                driverPage.getNumber(),
                driverPage.getSize(),
                driverPage.getTotalElements(),
                driverPage.getTotalPages(),
                driverPage.isLast()
        );
    }

    // Mappers
    private Driver mapToEntity(DriverDTO driverDTO) {
        return Driver.builder()
                .driverId(driverDTO.getDriverId())
                .driverName(driverDTO.getDriverName())
                .driverLicense(driverDTO.getDriverLicense())
                .driverState(driverDTO.getDriverState())
                .driverLicenseExpirationDate(driverDTO.getDriverLicenseExpirationDate())
                .build();
    }

    private DriverDTO mapToDTO(Driver driver) {
        return DriverDTO.builder()
                .driverId(driver.getDriverId())
                .driverName(driver.getDriverName())
                .driverLicense(driver.getDriverLicense())
                .driverState(driver.getDriverState())
                .driverLicenseExpirationDate(driver.getDriverLicenseExpirationDate())
                .build();
    }
}