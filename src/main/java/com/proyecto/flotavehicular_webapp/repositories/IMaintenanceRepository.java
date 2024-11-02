package com.proyecto.flotavehicular_webapp.repositories;

import com.proyecto.flotavehicular_webapp.models.Car.MaintenanceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface IMaintenanceRepository extends JpaRepository<MaintenanceHistory, Long> {
    Page<MaintenanceHistory> findByCarId(Long carId, Pageable pageable);
    Page<MaintenanceHistory> findByCreatedAtBetween(Date startDate, Date endDate, Pageable pageable);

    Page<MaintenanceHistory> findByCarIdAndCreatedAtBetween(Long carId, Date startDate, Date endDate, Pageable pageable);
}
