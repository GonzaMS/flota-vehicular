package com.proyecto.flotavehicular_webapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class AssignedOrderDTO implements Serializable {

    private Long assignedOrderId;

    private Date assignedDate;

    @NotBlank(message = "Itinerary is required")
    private String itinerary;

    @NotNull(message = "CarId is required")
    private Long carId;

    @NotNull(message = "DriverId is required")
    private Long driverId;

    @NotNull(message = "TravelOrderId is required")
    private Long travelOrderId;
}