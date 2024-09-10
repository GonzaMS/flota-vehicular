package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CarReports {

    private final ICarRepository carRepository;

    public CarReports(ICarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public void exportReport(HttpServletResponse response) throws JRException, IOException {
        List<Car> vehiculos = carRepository.findAll();

        // Load template .jrxml
        InputStream templateStream = getClass().getResourceAsStream("/reports/car_reports.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

        // Fill the report with the data
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(vehiculos);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Sistema de Gestión de Vehículos");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Set the content type of the report (PDF)
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_vehiculos.pdf");

        // Export the report to PDF and write it to the HTTP response
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }
}