package com.proyecto.flotavehicular_webapp.dto;

import com.proyecto.flotavehicular_webapp.models.Driver;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class DriverIncidentsDTO {
    private Long incidentId;

    @NotBlank(message = "Description is required")
    private String incidentDescription;
    private Date incidentDate;
    @NotBlank(message = "Type is required")
    private String incidentType;
    @NotNull(message = "Driver id is required")
    private Long driverId;
}
