package com.proyecto.flotavehicular_webapp.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cars_incidents")
public class CarIncidense {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long incidenseId;

    private String incidenseDescription;
    private Date incidenseDate;

    //Relaciones
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;
}
