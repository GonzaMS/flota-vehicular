package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.DriverDTO;
import com.proyecto.flotavehicular_webapp.models.Driver;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;

public interface IDriverService {

    DriverDTO getDriverById(Long id);

    Driver saveDriver(DriverDTO driverDTO);

    DriverDTO updateDriver(Long id, DriverDTO driverDTO);

    void deleteDriver(Long id);

    PageResponse<DriverDTO> getAllDrivers(int pageNumber, int pageSize);

    PageResponse<DriverDTO> getDriverByState(String state, int pageNumber, int pageSize);

    PageResponse<DriverDTO> getDriverByName(String name, int pageNumber, int pageSize);

    PageResponse<DriverDTO> getDriverByLicense(String license, int pageNumber, int pageSize);
}
