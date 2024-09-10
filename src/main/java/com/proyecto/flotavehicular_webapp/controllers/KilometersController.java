package com.proyecto.flotavehicular_webapp.controllers;


import com.proyecto.flotavehicular_webapp.dto.KilometersDTO;
import com.proyecto.flotavehicular_webapp.models.Kilometers;
import com.proyecto.flotavehicular_webapp.services.IKilometersService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/kilometers")
public class KilometersController {

    private final IKilometersService kilometersService;

    public KilometersController(IKilometersService kilometersService) {
        this.kilometersService = kilometersService;
    }


    @GetMapping
    public ResponseEntity<PageResponse<KilometersDTO>> getAllKilometers(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam (defaultValue = "10") int pageSize) {

        PageResponse<KilometersDTO> kilometersPageResponse = kilometersService.getAllKilometers(pageNumber, pageSize);

        if (kilometersPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(kilometersPageResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KilometersDTO> getKilometersById(@PathVariable Long id){
        KilometersDTO kilometersDTO = kilometersService.getKilometersById(id);
        return ResponseEntity.ok(kilometersDTO);
    }

    @PostMapping
    public ResponseEntity<Kilometers> saveKilometers(@Valid  @RequestBody KilometersDTO kilometersDTO){
        Kilometers newKilometers = kilometersService.saveKilometers(kilometersDTO);

        if(newKilometers == null){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(newKilometers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KilometersDTO> updateKilometers(
            @PathVariable Long id,
            @RequestBody KilometersDTO kilometersDTO) {

        kilometersService.updateKilometers(id, kilometersDTO);

        KilometersDTO updatedKilometers = kilometersService.getKilometersById(id);

        return ResponseEntity.ok().body(updatedKilometers);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKilometers(@PathVariable Long id){
        kilometersService.deleteKilometers(id);
        return ResponseEntity.noContent().build();
    }


    // Filters
    @GetMapping("/car/{carId}")
    public ResponseEntity<PageResponse<KilometersDTO>> getKilometersByCarId(
            @PathVariable Long carId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize){

        PageResponse<KilometersDTO> kilometersPageResponse = kilometersService.getKilometersByCarId(carId, pageNumber, pageSize);

        if (kilometersPageResponse.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(kilometersPageResponse);
    }
}
