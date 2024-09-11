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

        // Load template .jrxml (consider using .jasper precompiled file if available)
        try (InputStream templateStream = getClass().getResourceAsStream("/reports/car_reports.jrxml")) {
            if (templateStream == null) {
                throw new JRException("The report template file was not found.");
            }

            // Compile the .jrxml file
            JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

            // Prepare the data source
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(vehiculos);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("createdBy", "Sistema de Gestión de Vehículos");

            // Fill the report with data
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Set the content type for the response
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_vehiculos.pdf");

            // Export the report as a PDF stream to the HTTP response
            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
        }
    }
}
