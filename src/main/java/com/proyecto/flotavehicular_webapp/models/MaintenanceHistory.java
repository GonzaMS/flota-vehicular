package com.proyecto.flotavehicular_webapp.models;

import com.proyecto.flotavehicular_webapp.enums.EMAINTENANCE;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

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
    private long maintenanceId;

    private String serviceDescription;

    private double serviceCost;

    @CreatedDate
    private Date serviceDate;

    @Enumerated(EnumType.STRING)
    private EMAINTENANCE type;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

}