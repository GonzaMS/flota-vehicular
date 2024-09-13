package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.TravelObservationDTO;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;

public interface ITravelObservationService {

    PageResponse<TravelObservationDTO> getAllTravelObservations(int pageNumber, int pageSize);

    TravelObservationDTO getTravelObservationById(Long id);

    TravelObservationDTO createTravelObservation(TravelObservationDTO travelObservationDTO);

    void updateTravelObservation(Long id, TravelObservationDTO travelObservationDTO);

    void deleteTravelObservation(Long id);

    //PageResponse<TravelObservationDTO> getObservationsByDriver(Long driverId, int pageNumber, int pageSize);
}
