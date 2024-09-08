package com.proyecto.flotavehicular_webapp.models;

import com.proyecto.flotavehicular_webapp.enums.EORDERSSTATE;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "travel_orders")
public class TravelOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long travelOrderId;

    private String client;
    private Date travelLeaveDate;
    private Date travelArriveDate;

    @Enumerated(EnumType.STRING)
    private EORDERSSTATE travelOrderState;

}