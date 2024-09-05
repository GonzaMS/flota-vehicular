package com.proyecto.flotavehicular_webapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "incidents")
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long incidentId;

    private String incidentDescription;
    private Date incidentDate;
    private String incidentType;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;
}
