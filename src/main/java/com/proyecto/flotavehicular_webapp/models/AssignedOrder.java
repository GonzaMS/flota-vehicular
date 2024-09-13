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

    private Long travelOrderId;

    @PrePersist
    protected void onCreate() {
        if (this.assignedDate == null) {
            this.assignedDate = new Date();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.assignedDate == null) {
            this.assignedDate = new Date();
        }
    }

    /*@ManyToOne
    @JoinColumn(name = "travel_order_id")
    private TravelOrder travelOrder;
     */
}