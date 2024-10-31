package com.proyecto.flotavehicular_webapp.dto;

import com.proyecto.flotavehicular_webapp.dto.driver.DriverDTO;
import com.proyecto.flotavehicular_webapp.dto.driver.DriverIncidentsDTO;
import lombok.Data;

@Data
public class DriverAndIncidentDTO {

    private DriverDTO driverDTO;
    private DriverIncidentsDTO driverIncidentsDTO;

    // Getters y setters
    public DriverDTO getdriverDTO() {return driverDTO; }

    public void setDriverDTO(DriverDTO driverDTO) {
        this.driverDTO = driverDTO;
    }

    public DriverIncidentsDTO getDriverIncidentsDTO() {
        return driverIncidentsDTO;
    }

    public void setDriverIncidentsDTO(DriverIncidentsDTO driverIncidentsDTO) {
        this.driverIncidentsDTO = driverIncidentsDTO;
    }
}