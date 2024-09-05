package com.proyecto.flotavehicular_webapp.models;

import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "drivers")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long driverId;

    private String driverName;
    private String driverLicense;
    private Date driverLicenseExpirationDate;

    @Enumerated(EnumType.STRING)
    private ESTATES driverState;
}
