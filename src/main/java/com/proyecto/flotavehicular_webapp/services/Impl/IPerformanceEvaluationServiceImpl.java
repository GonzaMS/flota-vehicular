package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.PerformanceEvaluationDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Driver;
import com.proyecto.flotavehicular_webapp.repositories.IDriverRepository;
import com.proyecto.flotavehicular_webapp.models.PerformanceEvaluation;
import com.proyecto.flotavehicular_webapp.repositories.IPerformanceEvaluationRepository;
import com.proyecto.flotavehicular_webapp.services.IPerformanceEvaluationService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IPerformanceEvaluationServiceImpl implements IPerformanceEvaluationService {

    private final IPerformanceEvaluationRepository performanceEvaluationRepository;
    private final IDriverRepository driverRepository;

    private static final String NOT_FOUND = "Performance evaluation not found";

    public IPerformanceEvaluationServiceImpl(IPerformanceEvaluationRepository performanceEvaluationRepository, IDriverRepository driverRepository) {
        this.performanceEvaluationRepository = performanceEvaluationRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceEvaluationDTO getEvaluationById(Long id) {
        PerformanceEvaluation evaluation = performanceEvaluationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));
        return mapToDTO(evaluation);
    }

    @Override
    @Transactional
    public PerformanceEvaluation saveEvaluation(PerformanceEvaluationDTO performanceEvaluationDTO) {
        Driver driver = driverRepository.findById(performanceEvaluationDTO.getDriverId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));

        PerformanceEvaluation evaluation = mapToEntity(performanceEvaluationDTO);
        evaluation.setDriver(driver);
        return performanceEvaluationRepository.save(evaluation);
    }


    @Override
    @Transactional
    public void updateEvaluation(Long id, PerformanceEvaluationDTO performanceEvaluationDTO) {
        PerformanceEvaluation evaluation = performanceEvaluationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));

        evaluation.setPerformanceDate(performanceEvaluationDTO.getPerformanceDate());
        evaluation.setPerformancePoints(performanceEvaluationDTO.getPerformancePoints());
        evaluation.getDriver().setDriverId(performanceEvaluationDTO.getDriverId());

        performanceEvaluationRepository.save(evaluation);
    }

    @Override
    @Transactional
    public void deleteEvaluation(Long id) {
        PerformanceEvaluation evaluation = performanceEvaluationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));
        performanceEvaluationRepository.delete(evaluation);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PerformanceEvaluationDTO> getAllEvaluations(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<PerformanceEvaluation> evaluationsPage = performanceEvaluationRepository.findAll(pageable);

        List<PerformanceEvaluationDTO> evaluationDTOList = evaluationsPage.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                evaluationDTOList,
                evaluationsPage.getNumber(),
                evaluationsPage.getSize(),
                evaluationsPage.getTotalElements(),
                evaluationsPage.getTotalPages(),
                evaluationsPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PerformanceEvaluationDTO> getPerformanceByDriverName(String name, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<PerformanceEvaluation> evaluationsPage = performanceEvaluationRepository.findByDriver_DriverName(name, pageable);

        if (evaluationsPage.isEmpty()) {
            throw new NotFoundException("Performance evaluations for driver name not found: " + name);
        }

        List<PerformanceEvaluationDTO> evaluationDTOList = evaluationsPage.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                evaluationDTOList,
                evaluationsPage.getNumber(),
                evaluationsPage.getSize(),
                evaluationsPage.getTotalElements(),
                evaluationsPage.getTotalPages(),
                evaluationsPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PerformanceEvaluationDTO> findByPerformanceDate(Date performanceDate, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<PerformanceEvaluation> evaluationsPage = performanceEvaluationRepository.findByPerformanceDate(performanceDate, pageable);

        if (evaluationsPage.isEmpty()) {
            throw new NotFoundException("Performance evaluations for date not found: " + performanceDate);
        }

        List<PerformanceEvaluationDTO> evaluationDTOList = evaluationsPage.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                evaluationDTOList,
                evaluationsPage.getNumber(),
                evaluationsPage.getSize(),
                evaluationsPage.getTotalElements(),
                evaluationsPage.getTotalPages(),
                evaluationsPage.isLast()
        );
    }

    // Mappers
    private PerformanceEvaluationDTO mapToDTO(PerformanceEvaluation evaluation) {
        return PerformanceEvaluationDTO.builder()
                .performanceId(evaluation.getPerformanceId())
                .performanceDate(evaluation.getPerformanceDate())
                .performancePoints(evaluation.getPerformancePoints())
                .driverId(evaluation.getDriver().getDriverId())
                .build();
    }

    private PerformanceEvaluation mapToEntity(PerformanceEvaluationDTO dto) {
        PerformanceEvaluation evaluation = new PerformanceEvaluation();
        evaluation.setPerformanceId(dto.getPerformanceId());
        evaluation.setPerformanceDate(dto.getPerformanceDate());
        evaluation.setPerformancePoints(dto.getPerformancePoints());

        return evaluation;
    }
}
