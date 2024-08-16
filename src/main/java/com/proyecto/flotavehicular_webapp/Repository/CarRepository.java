package com.proyecto.flotavehicular_webapp.Repository;

import com.proyecto.flotavehicular_webapp.Models.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car,Long> {

}
