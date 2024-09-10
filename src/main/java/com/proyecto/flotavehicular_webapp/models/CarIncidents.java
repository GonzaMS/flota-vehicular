package com.proyecto.flotavehicular_webapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "incidents")
public class CarIncidents {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long incidentId;

    private String incidentDescription;

    @Temporal(TemporalType.DATE)
    private Date incidentDate;
    private String incidentType;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;


    @PrePersist
    protected void onCreate() {
        if (this.incidentDate == null) {
            this.incidentDate = new Date();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.incidentDate = new Date();
    }
}
