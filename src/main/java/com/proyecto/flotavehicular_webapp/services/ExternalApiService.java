package com.proyecto.flotavehicular_webapp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.flotavehicular_webapp.models.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class ExternalApiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ExternalApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Car callExternalApi(Long id) {
        String externalApiUrl = "http://localhost:8083/api/v1/cars/" + id;

        ResponseEntity<String> response = restTemplate.getForEntity(externalApiUrl, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();

            try {
                // Convierte el JSON a un objeto CarDTO
                return objectMapper.readValue(responseBody, Car.class);
            } catch (Exception e) {
                throw new RuntimeException("Error al convertir la respuesta de la API en CarDTO", e);
            }
        } else {
            throw new RuntimeException("Error al consumir la API externa");
        }
    }
}
