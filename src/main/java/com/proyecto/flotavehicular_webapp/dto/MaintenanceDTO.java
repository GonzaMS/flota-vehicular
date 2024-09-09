package com.proyecto.flotavehicular_webapp.dto;

import com.proyecto.flotavehicular_webapp.enums.EMAINTENANCE;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Builder
public class MaintenanceDTO {
    private long maintenanceId;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date maintenanceDate;

    @NotBlank(message = "Description is mandatory")
    private String maintenanceDescription;

    @NotNull(message = "Maintenance cost is mandatory")
    @DecimalMin(value = "0.0", inclusive = false, message = "Maintenance cost must be greater than 0")
    @DecimalMax(value = "10000.0", message = "Maintenance cost must be less than or equal to 10000")
    private double maintenanceCost;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Maintenance type is mandatory")
    private EMAINTENANCE maintenanceType;

    @NotNull(message = "Car id is mandatory")
    private long carId;
}
