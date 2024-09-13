package com.proyecto.flotavehicular_webapp.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class CarIncidentsDTO implements Serializable {

    private Long incidentId;

    @NotBlank(message = "CarIncidents is required")
    private String incidentDescription;

    private Date incidentDate;

    @NotBlank(message = "CarIncidents type is required")
    private String incidentType;

    @NotNull(message = "CarId is required")
    private Long carId;
}
