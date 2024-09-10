package com.proyecto.flotavehicular_webapp.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "performance_evaluations")
public class PerformanceEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long performanceId;

    private Date performanceDate;
    private Integer performancePoints;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;
}
