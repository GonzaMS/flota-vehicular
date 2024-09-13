package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.PerformanceEvaluationDTO;
import com.proyecto.flotavehicular_webapp.models.PerformanceEvaluation;
import com.proyecto.flotavehicular_webapp.services.IPerformanceEvaluationService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/performance-evaluations")
public class PerformanceEvaluationController {

    private final IPerformanceEvaluationService performanceEvaluationService;

    public PerformanceEvaluationController(IPerformanceEvaluationService performanceEvaluationService) {
        this.performanceEvaluationService = performanceEvaluationService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<PerformanceEvaluationDTO>> getAllEvaluations(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageResponse<PerformanceEvaluationDTO> response = performanceEvaluationService.getAllEvaluations(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceEvaluationDTO> getEvaluationById(@PathVariable Long id) {
        PerformanceEvaluationDTO evaluationDTO = performanceEvaluationService.getEvaluationById(id);
        return ResponseEntity.ok(evaluationDTO);
    }

    @PostMapping
    public ResponseEntity<PerformanceEvaluation> savePerformanceEvaluation(@Valid @RequestBody PerformanceEvaluationDTO performanceEvaluationDTO) {
        PerformanceEvaluation newEvaluation = performanceEvaluationService.saveEvaluation(performanceEvaluationDTO);
        if (newEvaluation == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newEvaluation);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePerformanceEvaluation(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceEvaluationDTO performanceEvaluationDTO) {
        performanceEvaluationService.updateEvaluation(id, performanceEvaluationDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformanceEvaluation(@PathVariable Long id) {
        performanceEvaluationService.deleteEvaluation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-date")
    public ResponseEntity<PageResponse<PerformanceEvaluationDTO>> findByPerformanceDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date performanceDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageResponse<PerformanceEvaluationDTO> response = performanceEvaluationService.findByPerformanceDate(performanceDate, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-driver")
    public ResponseEntity<PageResponse<PerformanceEvaluationDTO>> getPerformanceByDriverName(
            @RequestParam String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageResponse<PerformanceEvaluationDTO> response = performanceEvaluationService.getPerformanceByDriverName(name, page, size);
        return ResponseEntity.ok(response);
    }
}