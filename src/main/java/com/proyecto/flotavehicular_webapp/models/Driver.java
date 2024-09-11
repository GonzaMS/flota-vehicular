package com.proyecto.flotavehicular_webapp.models;

import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "drivers")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long driverId;

    private String driverName;
    private String driverLicense;
    private Date driverLicenseExpirationDate;

    @Enumerated(EnumType.STRING)
    private ESTATES driverState;

}
