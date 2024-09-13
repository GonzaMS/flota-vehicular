package com.proyecto.flotavehicular_webapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class KilometersDTO implements Serializable {

    private Long kilometersId;

    @NotNull(message = "Actual km is required")
    private Integer actualKm;

    private Date updateKmDate;

    @NotNull(message = "CarId is required")
    private Long carId;
}
