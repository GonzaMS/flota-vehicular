package com.proyecto.flotavehicular_webapp.repositories;

import com.proyecto.flotavehicular_webapp.models.Driver.DrivingHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDrivingHistoryRepository extends JpaRepository<DrivingHistory, Long> {

    Page<DrivingHistory> findByDriverDriverId(Long driverId, Pageable pageable);

    Page<DrivingHistory> findByCar_Id(Long carId, Pageable pageable);
}
