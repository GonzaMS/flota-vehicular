package com.proyecto.flotavehicular_webapp.dto;


import com.proyecto.flotavehicular_webapp.dto.driver.DriverDTO;
import com.proyecto.flotavehicular_webapp.dto.driver.DriverIncidentsDTO;
import com.proyecto.flotavehicular_webapp.dto.driver.DrivingHistoryDTO;
import com.proyecto.flotavehicular_webapp.dto.driver.PerformanceEvaluationDTO;
import com.proyecto.flotavehicular_webapp.dto.travel.AssignedOrderDTO;
import lombok.Data;

import java.util.List;

@Data
public class DriverUpdateRequestDTO {

    private DriverDTO driverDTO;
    private List<DriverIncidentsDTO> driverIncidentsDTOS;
    private List<AssignedOrderDTO> assignedOrderDTOS;
    private List<DrivingHistoryDTO> drivingHistoryDTOS;
    private List<PerformanceEvaluationDTO> performanceEvaluationDTOS;

}