package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.services.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumingController {

    private final ExternalApiService externalApiService;

    @Autowired
    public ConsumingController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @GetMapping("/api/consume/{id}")
    public Car consumeExternalApi(@PathVariable Long id) {
        return externalApiService.callExternalApi(id);
    }
}
