package com.proyecto.flotavehicular_webapp.models;

import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long carId;

    private String carLicensePlate;
    private String carBrand;
    private String carModel;
    private String carFabricationYear;

    @Enumerated(EnumType.STRING)
    private ESTATES carState;
}
