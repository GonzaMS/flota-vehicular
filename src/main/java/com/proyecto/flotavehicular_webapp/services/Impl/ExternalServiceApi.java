package com.proyecto.flotavehicular_webapp.services.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.flotavehicular_webapp.dto.car.KilometersDTO;
import com.proyecto.flotavehicular_webapp.dto.driver.DrivingHistoryDTO;
import com.proyecto.flotavehicular_webapp.models.Car.Kilometers;
import com.proyecto.flotavehicular_webapp.models.Driver.DrivingHistory;
import com.proyecto.flotavehicular_webapp.models.Travel.AssignedOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalServiceApi {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ExternalServiceApi(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public AssignedOrder callExternalApi(Long id) {
        String externalApiUrl = "http://localhost:8083/api/v1/assigned-orders/" + id;

        ResponseEntity<String> response = restTemplate.getForEntity(externalApiUrl, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();

            try {
                // Convierte el JSON a un objeto CarDTO
                return objectMapper.readValue(responseBody, AssignedOrder.class);
            } catch (Exception e) {
                throw new RuntimeException("Error al convertir la respuesta de la API en CarDTO", e);
            }
        } else {
            throw new RuntimeException("Error al consumir la API externa");
        }
    }

    // Get By Id
    public Kilometers callExternalApiKilometers(Long id) {
        String externalApiUrl = "http://localhost:8083/api/v1/kilometers/" + id;

        ResponseEntity<String> response = restTemplate.getForEntity(externalApiUrl, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();

            try {
                // Convierte el JSON a un objeto CarDTO
                return objectMapper.readValue(responseBody, Kilometers.class);
            } catch (Exception e) {
                throw new RuntimeException("Error al convertir la respuesta de la API en CarDTO", e);
            }
        } else {
            throw new RuntimeException("Error al consumir la API externa");
        }
    }

    // Create kilometer
    public Kilometers callExternalApiCreateKilometers(KilometersDTO kilometersDTO) {
        String externalApiUrl = "http://localhost:8083/api/v1/kilometers";

        ResponseEntity<String> response = restTemplate.postForEntity(externalApiUrl, kilometersDTO, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();

            try {
                return objectMapper.readValue(responseBody, Kilometers.class);
            } catch (Exception e) {
                throw new RuntimeException("Error al convertir la respuesta de la API en CarDTO", e);
            }
        } else {
            throw new RuntimeException("Error al consumir la API externa");
        }
    }


    // Get By Id
    public DrivingHistory callExternalApiDrivingHistory(Long id) {
        String externalApiUrl = "http://localhost:8083/api/v1/driving-history/" + id;

        ResponseEntity<String> response = restTemplate.getForEntity(externalApiUrl, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();

            try {
                // Convierte el JSON a un objeto CarDTO
                return objectMapper.readValue(responseBody, DrivingHistory.class);
            } catch (Exception e) {
                throw new RuntimeException("Error al convertir la respuesta de la API en CarDTO", e);
            }
        } else {
            throw new RuntimeException("Error al consumir la API externa");
        }
    }

    // Create DrivingHistory
    public DrivingHistory callExternalApiCreateDrivingHistory(DrivingHistoryDTO drivingHistoryDTO) {
        String externalApiUrl = "http://localhost:8083/api/v1/driving-history";

        ResponseEntity<String> response = restTemplate.postForEntity(externalApiUrl, drivingHistoryDTO, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();

            try {
                return objectMapper.readValue(responseBody, DrivingHistory.class);
            } catch (Exception e) {
                throw new RuntimeException("Error al convertir la respuesta de la API en CarDTO", e);
            }
        } else {
            throw new RuntimeException("Error al consumir la API externa");
        }
    }

}