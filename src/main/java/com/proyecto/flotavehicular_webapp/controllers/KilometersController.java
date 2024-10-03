package com.proyecto.flotavehicular_webapp.controllers;


import com.proyecto.flotavehicular_webapp.dto.KilometersDTO;
import com.proyecto.flotavehicular_webapp.models.Kilometers;
import com.proyecto.flotavehicular_webapp.services.IKilometersService;
import com.proyecto.flotavehicular_webapp.utils.DateRange;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/kilometers")
public class KilometersController {

    @Value("${page.size}")
    private int defaultPageSize;

    private final IKilometersService kilometersService;

    public KilometersController(IKilometersService kilometersService) {
        this.kilometersService = kilometersService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<KilometersDTO>> getAllKilometers(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<KilometersDTO> kilometersPageResponse = kilometersService.getAll(pageNumber, effectivePageSize);

        if (kilometersPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(kilometersPageResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KilometersDTO> getKilometersById(@PathVariable Long id) {
        KilometersDTO kilometersDTO = kilometersService.getById(id);
        return ResponseEntity.ok(kilometersDTO);
    }

    @PostMapping
    public ResponseEntity<Kilometers> saveKilometers(@Valid @RequestBody KilometersDTO kilometersDTO) {
        Kilometers newKilometers = kilometersService.save(kilometersDTO);
        return ResponseEntity.ok(newKilometers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KilometersDTO> updateKilometers(@PathVariable Long id, @RequestBody KilometersDTO kilometersDTO) {

        kilometersService.update(id, kilometersDTO);

        KilometersDTO updatedKilometers = kilometersService.getById(id);

        return ResponseEntity.ok().body(updatedKilometers);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteKilometers(@PathVariable Long id) {
        kilometersService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Filters
    @GetMapping("/car/{carId}")
    public ResponseEntity<PageResponse<KilometersDTO>> getKilometersByCarId(
            @PathVariable Long carId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<KilometersDTO> kilometersPageResponse = kilometersService.getByCarId(carId, pageNumber, effectivePageSize);

        if (kilometersPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(kilometersPageResponse);
    }

    @PostMapping("/date")
    public ResponseEntity<PageResponse<KilometersDTO>> getKilometersByDate(
            @RequestBody @Valid DateRange dateRange,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<KilometersDTO> kilometersPageResponse = kilometersService.getByDate(dateRange.getStartDate(), dateRange.getEndDate(), pageNumber, effectivePageSize);

        if (kilometersPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(kilometersPageResponse);
    }

    @GetMapping("/car/{carId}/date")
    public ResponseEntity<PageResponse<KilometersDTO>> getKilometersByCarIdAndDate(
            @PathVariable Long carId,
            @RequestBody @Valid DateRange dateRange,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        int effectivePageSize = (pageSize != null) ? pageSize : defaultPageSize;

        PageResponse<KilometersDTO> kilometersPageResponse = kilometersService.getByCarIdAndDate(carId, dateRange.getStartDate(), dateRange.getEndDate(), pageNumber, effectivePageSize);

        if (kilometersPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(kilometersPageResponse);
    }
}
