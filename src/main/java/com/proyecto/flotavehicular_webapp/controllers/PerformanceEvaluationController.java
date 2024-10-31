package com.proyecto.flotavehicular_webapp.controllers;

import com.proyecto.flotavehicular_webapp.dto.driver.PerformanceEvaluationDTO;
import com.proyecto.flotavehicular_webapp.models.Driver.PerformanceEvaluation;
import com.proyecto.flotavehicular_webapp.services.IPerformanceEvaluationService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.utils.DateRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PerformanceEvaluation> savePerformanceEvaluation(@Valid @RequestBody PerformanceEvaluationDTO performanceEvaluationDTO) {
        PerformanceEvaluation newEvaluation = performanceEvaluationService.saveEvaluation(performanceEvaluationDTO);
        if (newEvaluation == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newEvaluation);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updatePerformanceEvaluation(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceEvaluationDTO performanceEvaluationDTO) {
        performanceEvaluationService.updateEvaluation(id, performanceEvaluationDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePerformanceEvaluation(@PathVariable Long id) {
        performanceEvaluationService.deleteEvaluation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/by-date")
    public ResponseEntity<PageResponse<PerformanceEvaluationDTO>> findByPerformanceDate(
            @RequestBody @Valid DateRange dateRange,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer pageSize) {

        int effectivePageSize = (pageSize != null) ? pageSize : 10;

        PageResponse<PerformanceEvaluationDTO> response = performanceEvaluationService.findByCreatedAtBetween(dateRange.getStartDate(), dateRange.getEndDate(), page, effectivePageSize);

        if (response.items().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

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
