package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.TravelOrderDTO;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;

public interface ITravelOrderService {

    PageResponse<TravelOrderDTO> getAllTravelOrders(int pageNumber, int pageSize);

    TravelOrderDTO getTravelOrderById(Long id);

    TravelOrderDTO createTravelOrder(TravelOrderDTO travelOrderDTO);

    void updateTravelOrder(Long id, TravelOrderDTO travelOrderDTO);

    void deleteTravelOrder(Long id);
}
