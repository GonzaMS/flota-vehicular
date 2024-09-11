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

import java.util.Date;
import java.util.List;

@Data
@Builder
public class MaintenanceDTO {

    private Long maintenanceId;

    private Date maintenanceDate;

    @NotBlank(message = "Description is required")
    private String maintenanceDescription;

    @NotNull(message = "Maintenance cost is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Maintenance cost must be greater than 0")
    @DecimalMax(value = "10000.0", message = "Maintenance cost must be less than or equal to 10000")
    private double maintenanceCost;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Maintenance type is required")
    private EMAINTENANCE maintenanceType;

    @NotNull(message = "CarId is required")
    private Long carId;

    private List<MaintenanceDTO> maintenanceHistory;
}
