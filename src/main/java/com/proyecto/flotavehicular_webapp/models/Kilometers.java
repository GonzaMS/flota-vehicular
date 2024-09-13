package com.proyecto.flotavehicular_webapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kilometers")
public class Kilometers {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long kilometersId;

    private Integer actualKm;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateKmDate;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @PrePersist
    protected void onCreate() {
        if (this.updateKmDate == null) {
            this.updateKmDate = new Date();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.updateKmDate == null) {
            this.updateKmDate = new Date();
        }
    }
}

