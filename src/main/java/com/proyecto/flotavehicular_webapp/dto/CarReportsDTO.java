package com.proyecto.flotavehicular_webapp.dto;

import com.proyecto.flotavehicular_webapp.dto.car.KilometersDTO;
import com.proyecto.flotavehicular_webapp.dto.car.MaintenanceHistoryDTO;
import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class CarReportsDTO implements Serializable {
    private Long id;
    private String brand;
    private String model;
    private String licensePlate;
    private String fabricationYear;
    private ESTATES state;

    private List<MaintenanceHistoryDTO> maintenanceHistories;
    private List<KilometersDTO> kilometers;
}
