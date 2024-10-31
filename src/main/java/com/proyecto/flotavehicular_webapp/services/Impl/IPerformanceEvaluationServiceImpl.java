package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.driver.PerformanceEvaluationDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Driver.Driver;
import com.proyecto.flotavehicular_webapp.repositories.IDriverRepository;
import com.proyecto.flotavehicular_webapp.models.Driver.PerformanceEvaluation;
import com.proyecto.flotavehicular_webapp.repositories.IPerformanceEvaluationRepository;
import com.proyecto.flotavehicular_webapp.services.IPerformanceEvaluationService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.utils.RedisUtils;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class IPerformanceEvaluationServiceImpl implements IPerformanceEvaluationService {

    private final IPerformanceEvaluationRepository performanceEvaluationRepository;
    private final IDriverRepository driverRepository;
    private final CacheManager cacheManager;

    private static final String NOT_FOUND = "Performance evaluation not found";

    private static final Logger logger = LoggerFactory.getLogger(IPerformanceEvaluationServiceImpl.class);

    public IPerformanceEvaluationServiceImpl(IPerformanceEvaluationRepository performanceEvaluationRepository,
                                             IDriverRepository driverRepository,
                                             CacheManager cacheManager) {
        this.performanceEvaluationRepository = performanceEvaluationRepository;
        this.driverRepository = driverRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PerformanceEvaluationDTO> getAllEvaluations(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<PerformanceEvaluation> evaluationsPage = performanceEvaluationRepository.findAll(pageable);

            evaluationsPage.forEach(evaluation -> {
                String key = RedisUtils.CacheKeyGenerator("api_performanceEvaluation_", evaluation.getPerformanceId());
                Cache cache = cacheManager.getCache(key);

                if (cache != null) {
                    Object evaluationOnCache = cache.get(key, Object.class);
                    if (evaluationOnCache == null) {
                        cache.put(key, evaluation);
                    }
                }
            });

            return mapToPageResponse(evaluationsPage);
        } catch (Exception e) {
            logger.error("Error getting all performance evaluations: {}", e.getMessage());
            throw new ServiceException("Error getting all performance evaluations");
        }
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_performanceEvaluation_', #id)")

    public PerformanceEvaluationDTO getEvaluationById(Long id) {
        PerformanceEvaluation evaluation = performanceEvaluationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));
        return mapToDTO(evaluation);
    }

    @Override
    @Transactional
    public PerformanceEvaluation saveEvaluation(PerformanceEvaluationDTO performanceEvaluationDTO) {
        try {
            Driver driver = driverRepository.findById(performanceEvaluationDTO.getDriverId())
                    .orElseThrow(() -> new NotFoundException(NOT_FOUND));

            PerformanceEvaluation evaluation = mapToEntity(performanceEvaluationDTO);
            evaluation.setDriver(driver);
            return performanceEvaluationRepository.save(evaluation);
        } catch (NotFoundException e) {
            logger.error("Driver with id {} not found", performanceEvaluationDTO.getDriverId());
            throw e;
        } catch (Exception e) {
            logger.error("Error saving performance evaluations: {}", e.getMessage());
            throw new ServiceException("Error saving performance evaluations");
        }
    }


    @Override
    @Transactional
    @CachePut(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_performanceEvaluation_', #id)")
    public PerformanceEvaluationDTO updateEvaluation(Long id, PerformanceEvaluationDTO performanceEvaluationDTO) {
        try {
            PerformanceEvaluation evaluation = performanceEvaluationRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(NOT_FOUND));

            evaluation.setPerformancePoints(performanceEvaluationDTO.getPerformancePoints());
            evaluation.getDriver().setDriverId(performanceEvaluationDTO.getDriverId());

            performanceEvaluationRepository.save(evaluation);
            return mapToDTO(evaluation);
        } catch (NotFoundException e) {
            logger.error("Evaluations with id {} not found", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating Evaluations: {}", e.getMessage());
            throw new ServiceException("Error updating evaluations");
        }
    }

    @Override
    @Transactional
    @CacheEvict(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_performanceEvaluation_', #id)")
    public void deleteEvaluation(Long id) {
        PerformanceEvaluation evaluation = performanceEvaluationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));
        performanceEvaluationRepository.delete(evaluation);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_performanceEvaluation_', #id)")
    public PageResponse<PerformanceEvaluationDTO> getPerformanceByDriverName(String name, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<PerformanceEvaluation> evaluationsPage = performanceEvaluationRepository.findByDriver_DriverName(name, pageable);

            if (evaluationsPage.isEmpty()) {
                throw new NotFoundException("Performance evaluations for driver name not found: " + name);
            }

            return mapToPageResponse(evaluationsPage);
        } catch (NotFoundException e) {
            logger.error("Maintenance not found for driver with name: {}", name);
            throw e;
        } catch (Exception e) {
            logger.error("Error getting maintenance by Driver name: {}", e.getMessage());
            throw new ServiceException("Error getting maintenance by driver name");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicularwebapp.utils.RedisUtils).CacheKeyGenerator('api_performanceEvaluation_', #startDate, #endDate)")
    public PageResponse<PerformanceEvaluationDTO> findByCreatedAtBetween(Date startDate, Date endDate, int pageNumber, int pageSize) {
        try {

            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<PerformanceEvaluation> evaluationsPage = performanceEvaluationRepository.findByCreatedAtBetween(startDate, endDate, pageable);

            if (evaluationsPage.isEmpty()) {
                throw new NotFoundException("Performance evaluations for date not found: between " + startDate + " and " + endDate);
            }
            return mapToPageResponse(evaluationsPage);
        } catch (NotFoundException e) {
            logger.error("Maintenance not found  between " + startDate + " and " + endDate);
            throw e;
        } catch (Exception e) {
            logger.error("Error getting maintenance by Driver date: {}", e.getMessage());
            throw new ServiceException("Error getting maintenance by date");
        }
    }

    // Mappers
    private PerformanceEvaluationDTO mapToDTO(PerformanceEvaluation evaluation) {
        return PerformanceEvaluationDTO.builder()
                .performanceId(evaluation.getPerformanceId())
                .createdAt(evaluation.getCreatedAt())
                .performancePoints(evaluation.getPerformancePoints())
                .driverId(evaluation.getDriver().getDriverId())
                .build();
    }

    private PerformanceEvaluation mapToEntity(PerformanceEvaluationDTO evaluationDTO) {
        return PerformanceEvaluation.builder()
                .performanceId(evaluationDTO.getPerformanceId())
                .createdAt(evaluationDTO.getCreatedAt())
                .performancePoints(evaluationDTO.getPerformancePoints())
                .build();
    }

    private PageResponse<PerformanceEvaluationDTO> mapToPageResponse(Page<PerformanceEvaluation> performanceEvaluationPage ) {
        List<PerformanceEvaluationDTO> PerformanceEvaluationDTO = performanceEvaluationPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                PerformanceEvaluationDTO,
                performanceEvaluationPage.getNumber(),
                performanceEvaluationPage.getSize(),
                performanceEvaluationPage.getTotalElements(),
                performanceEvaluationPage.getTotalPages(),
                performanceEvaluationPage.isLast());
    }
}
