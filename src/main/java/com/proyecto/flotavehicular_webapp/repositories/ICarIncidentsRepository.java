package com.proyecto.flotavehicular_webapp.repositories;

import com.proyecto.flotavehicular_webapp.models.CarIncidents;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ICarIncidentsRepository extends JpaRepository<CarIncidents, Long> {
    Page<CarIncidents> findByCarId(Long carId, Pageable pageable);

    Page<CarIncidents> findByCreatedAtBetween(Date startDate, Date endDate, Pageable pageable);
}
