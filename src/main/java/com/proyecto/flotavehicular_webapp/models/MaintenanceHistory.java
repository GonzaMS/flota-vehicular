package com.proyecto.flotavehicular_webapp.models;

import com.proyecto.flotavehicular_webapp.enums.EMAINTENANCE;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "maintenance_histories")
public class MaintenanceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long maintenanceId;

    private Date maintenanceDate;
    private String maintenanceDescription;
    private Double maintenanceCost;

    @Enumerated(EnumType.STRING)
    private EMAINTENANCE maintenanceType;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

}