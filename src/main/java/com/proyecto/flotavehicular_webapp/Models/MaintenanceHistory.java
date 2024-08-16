package com.proyecto.flotavehicular_webapp.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "maintenance_histories")
public class MaintenanceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long maintenance_id;
    private String description;
    private double cost;

    @Enumerated(EnumType.STRING)
    private MaintenanceType type;

    @CreatedDate
    private Date date;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

}

enum MaintenanceType {
    REPAIR, MAINTENANCE
}