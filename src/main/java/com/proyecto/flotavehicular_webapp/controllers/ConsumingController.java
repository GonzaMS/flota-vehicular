package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.models.Car.Car;
import com.proyecto.flotavehicular_webapp.services.ExternalApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ConsumingController {

    private final ExternalApiService externalApiService;

    @Autowired
    public ConsumingController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @GetMapping("/api/consume/{id}")
    public Car consumeExternalApi(@PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        log.error("token",token);
        return externalApiService.callExternalApi(id, token);
    }
}

