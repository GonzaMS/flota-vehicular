package com.proyecto.flotavehicular_webapp.dto;

import com.proyecto.flotavehicular_webapp.models.Driver;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class TravelObservationDTO {

    private Long observationId;

    @NotBlank(message = "Description is required")
    private String observationDesc;

    private @NotNull(
            message = "Travel Order is required"
    ) Long travelOrderId;

    private @NotNull(
            message = "Driver is required"
    ) Driver driver;
}
