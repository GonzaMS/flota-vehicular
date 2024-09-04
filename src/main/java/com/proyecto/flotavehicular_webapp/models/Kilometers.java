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
@Table(name = "kilometers")
public class Kilometers {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long kilometersId;

    private Integer actualKm;
    private LocalDate updateKmDate;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;
}
