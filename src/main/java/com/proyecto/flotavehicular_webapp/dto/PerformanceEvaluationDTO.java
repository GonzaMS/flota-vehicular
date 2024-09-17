package com.proyecto.flotavehicular_webapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class PerformanceEvaluationDTO implements Serializable {

    private Long performanceId;

    private Date performanceDate;

    @NotNull(message = "The performance points is mandatory")
    @Min(value = 0, message = "The performance points must be greater than 0")
    @Max(value = 5, message = "The performance points must be less than 5")
    private Integer performancePoints;

    @NotNull(message = "The driver id is mandatory")
    private Long driverId;
}