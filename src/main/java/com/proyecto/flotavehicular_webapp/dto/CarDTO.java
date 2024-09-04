package com.proyecto.flotavehicular_webapp.dto;

import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarDTO {

    private long carId;

    @NotBlank(message = "License plate is mandatory")
    private String carBrand;

    @NotBlank(message = "Model is mandatory")
    private String carModel;

    @NotBlank(message = "Fabrication year is mandatory")
    private String carLicensePlate;

    @NotBlank(message = "Fabrication year is mandatory")
    private String carFabricationYear;

    @NotNull(message = "Car state is mandatory")
    private ESTATES carState;
}
