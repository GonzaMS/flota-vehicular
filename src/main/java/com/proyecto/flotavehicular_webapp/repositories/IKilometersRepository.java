package com.proyecto.flotavehicular_webapp.repositories;

import com.proyecto.flotavehicular_webapp.models.Kilometers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface IKilometersRepository extends JpaRepository<Kilometers, Long> {
    Page<Kilometers> findByCarId(Long carId, Pageable pageable);

    Page<Kilometers> findByCreatedAtBetween(Date startDate, Date endDate, Pageable pageable);

    Page<Kilometers> findByCarIdAndCreatedAtBetween(Long carId, Date startDate, Date endDate, Pageable pageable);
}
