package com.proyecto.flotavehicular_webapp.repositories;

import com.proyecto.flotavehicular_webapp.models.CarIncidents;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICarIncidentsRepository extends JpaRepository<CarIncidents, Long> {
    Page<CarIncidents> findByCar_CarId(Long carId, Pageable pageable);
}
