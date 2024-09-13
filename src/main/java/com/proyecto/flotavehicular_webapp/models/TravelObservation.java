package com.proyecto.flotavehicular_webapp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "travel_observations"
)
public class TravelObservation {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
    )
    private Long observationId;
    private String observationDesc;
    @OneToOne
    @JoinColumn(
            name = "travel_order_id"
    )
    private TravelOrder travelOrderID;
    @ManyToOne
    @JoinColumn(
            name = "driver_id"
    )
    private Driver driver;
}
