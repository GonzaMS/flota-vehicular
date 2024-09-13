package com.proyecto.flotavehicular_webapp.repositories;

import com.proyecto.flotavehicular_webapp.models.TravelObservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITravelObservationRepository extends JpaRepository<TravelObservation, Long> {
    Page<TravelObservation> findByTravelOrderID_TravelOrderId(Long travelOrderId, Pageable pageable);
    //Page<TravelObservation> findByDriver_Id(Long driverId, Pageable pageable);
}
