package com.proyecto.flotavehicular_webapp.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CarIncidentsDTO {
    private Long incidentId;

    @NotBlank(message = "CarIncidents is mandatory")
    private String incidentDescription;

    private Date incidentDate;

    @NotBlank(message = "CarIncidents type is mandatory")
    private String incidentType;

    @NotNull(message = "CarId is mandatory")
    private Long carId;
}
