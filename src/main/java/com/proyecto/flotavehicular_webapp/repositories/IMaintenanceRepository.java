package com.proyecto.flotavehicular_webapp.repositories;

import com.proyecto.flotavehicular_webapp.models.Car.MaintenanceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMaintenanceRepository extends JpaRepository<MaintenanceHistory, Long> {
    Page<MaintenanceHistory> findByCarId(Long carId, Pageable pageable);
}
