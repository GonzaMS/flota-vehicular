package com.proyecto.flotavehicular_webapp.models;


import com.proyecto.flotavehicular_webapp.enums.EASIGGNED;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "assigned_vehicles")
public class AssignedVehicles {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long assignedVehicleId;

    private Date assignedDate;


    //Relaciones
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
