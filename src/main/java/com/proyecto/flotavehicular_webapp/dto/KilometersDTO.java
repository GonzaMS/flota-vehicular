package com.proyecto.flotavehicular_webapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class KilometersDTO {

    private Long kilometersId;

    @NotNull(message = "Actual km is required")
    private Integer actualKm;

    private Date updateKmDate;

    @NotNull(message = "CarId is required")
    private Long carId;
}
