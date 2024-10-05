package com.proyecto.flotavehicular_webapp.repositories;

import com.proyecto.flotavehicular_webapp.models.PerformanceEvaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface IPerformanceEvaluationRepository extends JpaRepository<PerformanceEvaluation, Long> {

    Page<PerformanceEvaluation> findByDriver_DriverName(String driverName, Pageable pageable);

    Page<PerformanceEvaluation> findByCreatedAtBetween(Date startDate, Date endDate, Pageable pageable);
}
