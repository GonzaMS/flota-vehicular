package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.travel.TravelOrderDTO;
import com.proyecto.flotavehicular_webapp.models.Travel.TravelOrder;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;

public interface ITravelOrderService {
    PageResponse<TravelOrderDTO> getAll(int pageNumber, int pageSize);

    TravelOrderDTO getById(Long id);

    TravelOrder save(TravelOrderDTO travelOrderDTO);

    TravelOrderDTO update(Long id, TravelOrderDTO travelOrderDTO);

    void delete(Long id);

    void cancelTravelOrder(Long id);

    // Filter
    PageResponse<TravelOrderDTO> getByDriverId(Long driverId, int pageNumber, int pageSize);

    PageResponse<TravelOrderDTO> getByCarId(Long carId, int pageNumber, int pageSize);
}
