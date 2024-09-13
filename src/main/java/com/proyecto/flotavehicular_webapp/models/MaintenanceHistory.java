package com.proyecto.flotavehicular_webapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // Relationships
    @ManyToOne
    @JoinColumn(name = "car_id")
    @JsonIgnore
    private Car car;

    @PrePersist
    protected void onCreate() {
        if (this.maintenanceDate == null) {
            this.maintenanceDate = new Date();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.maintenanceDate == null) {
            this.maintenanceDate = new Date();
        }
    }

    @Override
    public String toString() {
        return "Fecha: " + maintenanceDate +
                "Desc: " + maintenanceDescription +
                "Costo: " + maintenanceCost +
                "Tipo: " + maintenanceType +
                "\n";
    }
}