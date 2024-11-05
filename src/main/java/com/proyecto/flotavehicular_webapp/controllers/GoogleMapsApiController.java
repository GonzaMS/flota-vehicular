package com.proyecto.flotavehicular_webapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
public class GoogleMapsApiController {

    private final String GOOGLE_API_KEY = "YOUR_GOOGLE_API_KEY";
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/calculate-distance")
    public Double calculateDistance(@RequestParam Double latOrigen, @RequestParam Double lngOrigen,
                                    @RequestParam Double latDestino, @RequestParam Double lngDestino) {
        String url = String.format(
                "https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&key=%s",
                latOrigen, lngOrigen, latDestino, lngDestino, GOOGLE_API_KEY);

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response != null && "OK".equals(response.get("status"))) {
            List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
            Map<String, Object> route = routes.get(0);
            Map<String, Object> leg = ((List<Map<String, Object>>) route.get("legs")).get(0);
            Map<String, Object> distance = (Map<String, Object>) leg.get("distance");
            return ((Number) distance.get("value")).doubleValue() / 1000; // Convert to kilometers
        }
        return null;
    }
}
