package com.proyecto.flotavehicular_webapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
public class KilometersDTO {

    private Long kilometersId;

    @NotNull(message = "Actual km is mandatory")
    private Integer actualKm;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String updateKmDate;

    @NotNull(message = "Car id is mandatory")
    private Long carId;
}
