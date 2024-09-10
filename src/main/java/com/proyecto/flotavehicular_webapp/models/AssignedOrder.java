package com.proyecto.flotavehicular_webapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "assigned_orders")
public class AssignedOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long assignedOrderId;

    private Date assignedDate;
    private String itinerary;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "travel_order_id")
    private TravelOrder travelOrder;
}
