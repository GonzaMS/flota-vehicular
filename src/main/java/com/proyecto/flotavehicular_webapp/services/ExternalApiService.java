package com.proyecto.flotavehicular_webapp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.flotavehicular_webapp.models.Car.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class ExternalApiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cars-url}")
    private String externalApiUrl;

    @Autowired
    public ExternalApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Car callExternalApi(Long id, String token) { // Añadir el token como parámetro

        externalApiUrl += id;

        // Crear encabezados HTTP, incluyendo el token JWT
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token); // Añadir el token en el encabezado

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Hacer la solicitud usando RestTemplate con los encabezados
        ResponseEntity<String> response = restTemplate.exchange(externalApiUrl, HttpMethod.GET, entity, String.class);

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
