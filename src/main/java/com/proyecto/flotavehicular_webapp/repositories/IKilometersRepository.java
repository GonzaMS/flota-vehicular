package com.proyecto.flotavehicular_webapp.repositories;

import com.proyecto.flotavehicular_webapp.models.Kilometers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IKilometersRepository extends JpaRepository<Kilometers, Long> {
}
