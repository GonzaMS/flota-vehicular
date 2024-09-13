package com.proyecto.flotavehicular_webapp.repositories;

import com.proyecto.flotavehicular_webapp.enums.EORDERSSTATE;
import com.proyecto.flotavehicular_webapp.models.TravelOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITravelOrderRepository extends JpaRepository<TravelOrder, Long> {

    Page<TravelOrder> findByTravelOrderState(EORDERSSTATE state, Pageable pageable);
    Page<TravelOrder> findByClient(String client, Pageable pageable);

}
