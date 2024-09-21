package com.proyecto.flotavehicular_webapp.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.flotavehicular_webapp.models.MaintenanceHistory;

import java.util.List;

public interface IMaintenanceRepository extends JpaRepository<MaintenanceHistory, Long> {
    Page<MaintenanceHistory> findByCar_CarId(Long carId, Pageable pageable);

    //List<MaintenanceHistory> findByCar_CarId(Long carId);
}
