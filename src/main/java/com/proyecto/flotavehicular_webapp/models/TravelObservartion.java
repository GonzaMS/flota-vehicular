package com.proyecto.flotavehicular_webapp.models;


import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "travel_observations")
public class TravelObservartion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long observationId;

    private String observationDesc;

    // Relationships
    @OneToOne
    @JoinColumn(name = "travel_order_id")
    private TravelOrder travelOrder;
}
