package com.proyecto.flotavehicular_webapp.models;


import jakarta.persistence.*;
import lombok.*;

import java.lang.runtime.ObjectMethods;
import java.util.Date;

@Data
@Builder
@Getter
@Setter
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

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;


}
