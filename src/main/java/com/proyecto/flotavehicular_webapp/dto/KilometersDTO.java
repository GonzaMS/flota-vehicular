package com.proyecto.flotavehicular_webapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class KilometersDTO {

    private Long kilometersId;

    @NotNull(message = "Actual km is mandatory")
    private Integer actualKm;

    private Date updateKmDate;

    @NotNull(message = "Car id is mandatory")
    private Long carId;
}
