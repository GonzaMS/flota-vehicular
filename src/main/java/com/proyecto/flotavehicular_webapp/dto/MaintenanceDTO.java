package com.proyecto.flotavehicular_webapp.dto;

import com.proyecto.flotavehicular_webapp.enums.EMAINTENANCE;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Data
@Builder
public class MaintenanceDTO {

    private long maintenanceId;

    @NotBlank(message = "Description is mandatory")
    private String serviceDescription;

    @NotBlank(message = "Cost is mandatory")
    private double serviceCost;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Maintenance type is mandatory")
    private EMAINTENANCE serviceType;

    @CreatedDate
    private Date serviceDate;
}
