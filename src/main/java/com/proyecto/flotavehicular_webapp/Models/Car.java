package com.proyecto.flotavehicular_webapp.Models;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long car_id;
    private String license_plate;
    private String brand;
    private String model;
    private String fabrication_year;

    @Enumerated(EnumType.STRING)
    private States state;

    //Relaciones
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<MaintenanceHistory> maintenanceHistories;

}

enum States {
    ACTIVE, INACTIVE
}
