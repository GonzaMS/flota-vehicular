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
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
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

    public Car callExternalApi(Long id, String token) {
        String requestUrl = externalApiUrl + "/" + id;

        // Log para depurar la URL de la API externa
        log.info("Making API call to URL: {}", requestUrl);

        // Crear encabezados HTTP, incluyendo el token JWT y especificar el tipo de contenido esperado
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        // Especificar que aceptamos JSON como respuesta
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Hacer la solicitud usando RestTemplate con los encabezados
        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            try {
                return objectMapper.readValue(responseBody, Car.class);
            } catch (Exception e) {
                log.error("Error converting response: {}", responseBody, e);
                throw new RuntimeException("Error al convertir la respuesta de la API en CarDTO", e);
            }
        } else {
            log.error("Error consuming external API - Status: {}, Response: {}", response.getStatusCode(), response.getBody());
            throw new RuntimeException("Error al consumir la API externa: " + response.getStatusCode());
        }
    }
}