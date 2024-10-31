package com.proyecto.flotavehicular_webapp.repositories;

import com.proyecto.flotavehicular_webapp.models.Driver.DriverIncidents;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDriverIncidentsRepository extends JpaRepository<DriverIncidents, Long> {
    Page<DriverIncidents> findByDriver_DriverId(Long driverId, Pageable pageable);
}
