package com.proyecto.flotavehicular_webapp.repositories;

import com.proyecto.flotavehicular_webapp.models.Travel.AssignedOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAssignedOrderRepository extends JpaRepository<AssignedOrder, Long> {
    Optional<AssignedOrder> findByDriver_driverIdAndCarId(Long driverId, Long carId);

}
