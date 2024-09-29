package com.proyecto.flotavehicular_webapp.repositories;

import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import com.proyecto.flotavehicular_webapp.models.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICarRepository extends JpaRepository<Car, Long> {

    Page<Car> findByState(ESTATES carState, Pageable pageable);

    Page<Car> findByBrand(String carBrand, Pageable pageable);

    Page<Car> findByModel(String carModel, Pageable pageable);

    Page<Car> findByLicensePlate(String carLicensePlate, Pageable pageable);
}
