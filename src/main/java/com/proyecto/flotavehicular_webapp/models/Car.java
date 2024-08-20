package com.proyecto.flotavehicular_webapp.models;

import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private long carId;

    private String licensePlate;

    private String brand;

    private String model;

    private String fabricationYear;

    @Enumerated(EnumType.STRING)
    private ESTATES carState;

    // Relaciones
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<MaintenanceHistory> maintenanceHistories;

}
