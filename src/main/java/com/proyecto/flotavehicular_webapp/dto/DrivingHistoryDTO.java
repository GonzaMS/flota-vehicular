package com.proyecto.flotavehicular_webapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class DrivingHistoryDTO implements Serializable {

    private Long drivingHistoryId;

    @NotNull(message = "Driving date is required")
    private Date drivingDate;

    @NotNull(message = "Kilometers driven are required")
    private Double kmDriven;

    @NotNull(message = "Driver id is required")
    private Long driverId;

    @NotNull(message = "Car id is required")
    private Long carId;
}
