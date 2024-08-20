package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.DriverDTO;
import com.proyecto.flotavehicular_webapp.models.Driver;

import java.util.List;

public interface IDriverService {
    List<DriverDTO> getAllDrivers();

    DriverDTO getDriverById(Long id);

    Driver saveDriver(DriverDTO driverDTO);

    void updateDriver(Long id, DriverDTO driverDTO);

    void deleteDriver(Long id);
}
