package com.proyecto.flotavehicular_webapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "driver_incidents")
public class DriverIncidents {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long incidentId;

    private String incidentDescription;

    @Temporal(TemporalType.DATE)
    private Date incidentDate;
    private String incidentType;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;


    @PrePersist
    protected void onCreate() {
        if (this.incidentDate == null) {
            this.incidentDate = new Date();
        }
    }
}
