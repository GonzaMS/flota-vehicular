package com.proyecto.flotavehicular_webapp.models;

import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long carId;

    private String carLicensePlate;
    private String carBrand;
    private String carModel;
    private String carFabricationYear;

    @Enumerated(EnumType.STRING)
    private ESTATES carState;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaintenanceHistory> maintenanceHistories;
}
