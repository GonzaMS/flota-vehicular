package com.proyecto.flotavehicular_webapp.models;


import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "travel_observations")
public class TravelObservartion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long observationId;

    private String observationDesc;

    // Relaciones
    @OneToOne
    @JoinColumn(name = "travel_order_id")
    private TravelOrder travelOrder;
}
