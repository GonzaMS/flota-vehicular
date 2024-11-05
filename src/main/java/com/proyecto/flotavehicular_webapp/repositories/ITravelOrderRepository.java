package com.proyecto.flotavehicular_webapp.repositories;

import com.proyecto.flotavehicular_webapp.models.Travel.TravelOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITravelOrderRepository extends JpaRepository<TravelOrder,Long> {
    Page<TravelOrder> findByDriverId(Long driverId, org.springframework.data.domain.Pageable pageable);
    Page<TravelOrder> findByCarId(Long carId, org.springframework.data.domain.Pageable pageable);
}
