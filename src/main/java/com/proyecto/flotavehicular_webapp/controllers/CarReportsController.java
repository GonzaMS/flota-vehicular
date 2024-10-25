package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.services.Reports.CarReports;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/car-reports")
public class CarReportsController {

    private final CarReports carReportsService;

    public CarReportsController(CarReports carReportsService) {
        this.carReportsService = carReportsService;
    }

    @GetMapping("/export")
    public void generateCarReports(HttpServletResponse response,
                                   @RequestParam(defaultValue = "0") int pageNumber,
                                   @RequestParam(defaultValue = "10") int pageSize) throws JRException, IOException {
        carReportsService.exportReport(response, pageNumber, pageSize);
    }
}
