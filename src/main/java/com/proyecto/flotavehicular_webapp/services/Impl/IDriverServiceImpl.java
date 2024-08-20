package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.DriverDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Driver;
import com.proyecto.flotavehicular_webapp.repositories.IDriverRepository;
import com.proyecto.flotavehicular_webapp.services.IDriverService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IDriverServiceImpl implements IDriverService {

    private static final String NOTFOUND  = "Driver not found";

    private final IDriverRepository driverRepository;

    public IDriverServiceImpl(IDriverRepository driverRepository){
        this.driverRepository = driverRepository;
    }

    @Override
    public List<DriverDTO> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();
        return drivers.stream().map(this::mapToDto).toList();

    }

    @Override
    public DriverDTO getDriverById(Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDto(driver);
    }

    @Override
    public Driver saveDriver(DriverDTO driverDTO) {
        Driver driver = mapToEntity(driverDTO);
        return driverRepository.save(driver);
    }

    @Override
    public void updateDriver(Long id, DriverDTO driverDTO) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        driver.setDriverId(driverDTO.getDriverId());
        driver.setName(driverDTO.getName());
        driver.setDriverLicense(driverDTO.getDriverLicense());
        driver.setLicenseExpirationDate(driverDTO.getLicenseExpirationDate());
        driver.setDriverState(driverDTO.getDriverState());
        driverRepository.save(driver);
    }

    @Override
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        driverRepository.delete(driver);
    }

    private Driver mapToEntity(DriverDTO driverDTO){
        return Driver.builder()
                .driverId(driverDTO.getDriverId())
                .name(driverDTO.getName())
                .driverLicense(driverDTO.getDriverLicense())
                .licenseExpirationDate(driverDTO.getLicenseExpirationDate())
                .driverState(driverDTO.getDriverState())
                .build();
    }

    // Mapping Object driver to his dto
    private DriverDTO mapToDto(Driver driver){
        return DriverDTO.builder()
                .driverId(driver.getDriverId())
                .name(driver.getName())
                .driverLicense(driver.getDriverLicense())
                .licenseExpirationDate(driver.getLicenseExpirationDate())
                .driverState(driver.getDriverState())
                .build();

    }
}
