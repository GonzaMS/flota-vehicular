package com.proyecto.flotavehicular_webapp.controllers;


import com.proyecto.flotavehicular_webapp.dto.car.KilometersDTO;
import com.proyecto.flotavehicular_webapp.dto.driver.DrivingHistoryDTO;
import com.proyecto.flotavehicular_webapp.models.Car.Kilometers;
import com.proyecto.flotavehicular_webapp.models.Driver.DrivingHistory;
import com.proyecto.flotavehicular_webapp.models.Travel.AssignedOrder;
import com.proyecto.flotavehicular_webapp.services.Impl.ExternalServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiConsumeController {

    private final ExternalServiceApi externalApiService;

    @Autowired
    public ApiConsumeController(ExternalServiceApi externalApiService) {
        this.externalApiService = externalApiService;
    }

    @GetMapping("/api/get/{id}")
    public AssignedOrder consumeExternalApi(@PathVariable Long id) {
        return externalApiService.callExternalApi(id);
    }

    @PostMapping("/api/post/kilometers")
    public Kilometers consumeExternalApiKilometers(@Validated @RequestBody KilometersDTO KilometersDTO) {
        return externalApiService.callExternalApiCreateKilometers(KilometersDTO);
    }

    @PostMapping("/api/post/driving-history")
    public DrivingHistory consumeExternalApiKilometers(@Validated @RequestBody DrivingHistoryDTO DrivingHistoryDTO) {
        return externalApiService.callExternalApiCreateDrivingHistory(DrivingHistoryDTO);
    }
}