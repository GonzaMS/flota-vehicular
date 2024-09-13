package com.proyecto.flotavehicular_webapp.models;

import com.proyecto.flotavehicular_webapp.enums.EORDERSSTATE;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "travel_orders"
)
public class TravelOrder {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
    )
    private Long travelOrderId;
    private String client;
    private LocalDateTime travelLeaveDate;
    private LocalDateTime travelArriveDate;
    @Enumerated(EnumType.STRING)
    private EORDERSSTATE travelOrderState;
}
