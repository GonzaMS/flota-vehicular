package com.proyecto.flotavehicular_webapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
    private LocalDate incidentDate;
    private String incidentType;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;
}
