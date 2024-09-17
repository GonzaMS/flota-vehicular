package com.proyecto.flotavehicular_webapp.dto;

import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class DriverDTO implements Serializable {

    private Long driverId;

    @NotBlank(message = "Name is mandatory")
    private String driverName;

    @NotBlank(message = "Last name is mandatory")
    private String driverLicense;

    @NotNull(message = "Driver state is mandatory")
    private ESTATES driverState;

    @NotNull(message = "Driver license expiration date is mandatory")
    private Date driverLicenseExpirationDate;

    private List<PerformanceEvaluationDTO> evaluations;

    private List<DrivingHistoryDTO> drivingHistories;

    private List<DriverIncidentsDTO> driverIncidents;
}
