package com.proyecto.flotavehicular_webapp.models;

import com.proyecto.flotavehicular_webapp.enums.EMAINTENANCE;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "maintenance_histories")
public class MaintenanceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long maintenanceId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date maintenanceDate;

    private String maintenanceDescription;
    private Double maintenanceCost;

    @Enumerated(EnumType.STRING)
    private EMAINTENANCE maintenanceType;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @PrePersist
    protected void onCreate() {
        if (this.maintenanceDate == null) {
            this.maintenanceDate = new Date();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.maintenanceDate = new Date();
    }
}