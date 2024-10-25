package com.proyecto.flotavehicular_webapp.dto;

import com.proyecto.flotavehicular_webapp.models.Car.Car;
import com.proyecto.flotavehicular_webapp.models.Car.Kilometers;
import com.proyecto.flotavehicular_webapp.models.Car.MaintenanceHistory;
import lombok.Data;

import java.util.List;

@Data
public class CarReportWrapper {
    private Car car;
    private List<MaintenanceHistory> maintenanceHistories;
    private List<Kilometers> kilometers;
}
