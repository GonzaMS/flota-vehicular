package com.proyecto.flotavehicular_webapp.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class DrivingHistoryDTO {

    private Long drivingHistoryId;
    private Long driverId;
    private Long carId;
    private Date drivingDate;
    private double kmDriven;
}
