package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.PerformanceEvaluationDTO;
import com.proyecto.flotavehicular_webapp.models.PerformanceEvaluation;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;

import java.util.Date;


public interface IPerformanceEvaluationService {

    PerformanceEvaluationDTO getEvaluationById(Long id);

    PerformanceEvaluation saveEvaluation(PerformanceEvaluationDTO performanceEvaluationDTO);

    PerformanceEvaluationDTO updateEvaluation(Long id, PerformanceEvaluationDTO performanceEvaluationDTO);

    void deleteEvaluation(Long id);

    PageResponse<PerformanceEvaluationDTO> getAllEvaluations(int pageNumber, int pageSize);

    PageResponse<PerformanceEvaluationDTO> getPerformanceByDriverName(String name, int pageNumber, int pageSize);

    PageResponse<PerformanceEvaluationDTO> findByCreatedAtBetween(Date starDate, Date endDate, int pageNumber, int pageSize);

}
