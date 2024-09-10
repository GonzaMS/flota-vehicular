package com.proyecto.flotavehicular_webapp.dto;

import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class DriverDTO {

    private Long driverId;

    @NotBlank(message = "Name is mandatory")
    private String driverName;

    @NotBlank(message = "Last name is mandatory")
    private String driverLicense;

    @NotNull(message = "Driver state is mandatory")
    private ESTATES driverState;

    @NotNull(message = "Driver license expiration date is mandatory")
    private Date driverLicenseExpirationDate;
}
