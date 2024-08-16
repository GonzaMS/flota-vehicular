package com.proyecto.flotavehicular_webapp.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarDTO {
    private long car_id;
    private String brand;
    private String model;
    private String license_plate;
    private String fabrication_year;
    private String state;

}
