package com.proyecto.flotavehicular_webapp.models;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "driving_history")
public class DrivingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long drivingHistoryId;

    private LocalDate drivingDate;
    private Double kmDriven;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "dirver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

}
