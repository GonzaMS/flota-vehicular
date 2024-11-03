package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.services.Reports.CarReports;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<byte[]> generateCarReport() {
        try {
            byte[] pdfContent = carReportsService.generateCarReport(); // Generar el PDF

            // Configurar los headers para la respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=car_report.pdf");
            headers.add("Content-Type", "application/pdf");
            headers.add("Content-Length", String.valueOf(pdfContent.length));

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK); // Retornar el PDF
        } catch (IOException e) {
            // Manejo de errores
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

