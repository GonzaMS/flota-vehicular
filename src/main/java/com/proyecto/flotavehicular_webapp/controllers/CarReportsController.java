package com.proyecto.flotavehicular_webapp.controllers;


import com.proyecto.flotavehicular_webapp.services.Reports.CarReports;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/api/v1/car-reports")
@Slf4j
public class CarReportsController {

    private final CarReports carReports;

    public CarReportsController(CarReports carReports) {
        this.carReports = carReports;
    }

    @GetMapping("/export")
    public void generateCarReports(HttpServletResponse response) {
        try {
            carReports.exportReport(response);
        } catch (JRException | IOException e) {
            log.error("Error exporting car reports", e);
        }
    }
}
