package com.proyecto.flotavehicular_webapp.controllers;


import com.proyecto.flotavehicular_webapp.services.CarReports;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/api/v1/car-reports")
public class CarReportsController {

    private final CarReports carReports;

    Logger logger = org.slf4j.LoggerFactory.getLogger(CarReportsController.class);


    public CarReportsController(CarReports carReports) {
        this.carReports = carReports;
    }

    @GetMapping("/export")
    public void generateCarReports(HttpServletResponse response) {
        try {
            carReports.exportReport(response);
        } catch (JRException | IOException e) {
            logger.error("Error exporting car reports", e);
        }
    }
}
