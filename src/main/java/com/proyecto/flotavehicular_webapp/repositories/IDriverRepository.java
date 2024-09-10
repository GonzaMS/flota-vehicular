package com.proyecto.flotavehicular_webapp.repositories;

import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import com.proyecto.flotavehicular_webapp.models.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDriverRepository extends JpaRepository<Driver, Long> {

    Page<Driver> findByDriverName(String driverName, Pageable pageable);

    Page<Driver> findByDriverLicense(String driverLicense, Pageable pageable);

    Page<Driver> findByDriverState(ESTATES driverState, Pageable pageable);

}
