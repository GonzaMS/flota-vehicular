package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.TravelObservationDTO;
import com.proyecto.flotavehicular_webapp.models.TravelObservation;
import com.proyecto.flotavehicular_webapp.models.TravelOrder;
import com.proyecto.flotavehicular_webapp.repositories.ITravelObservationRepository;
import com.proyecto.flotavehicular_webapp.repositories.ITravelOrderRepository;
import com.proyecto.flotavehicular_webapp.services.ITravelObservationService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TravelObservationServiceImpl implements ITravelObservationService {

    @Autowired
    private ITravelObservationRepository travelObservationRepository;

    @Autowired
    private ITravelOrderRepository travelOrderRepository; // Necesario para obtener la orden de viaje asociada

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TravelObservationDTO> getAllTravelObservations(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<TravelObservation> travelObservationPage = travelObservationRepository.findAll(pageable);

        List<TravelObservationDTO> dtos = travelObservationPage.stream().map(this::mapToDTO).toList();
        return PageResponse.of(dtos, travelObservationPage.getNumber(), travelObservationPage.getSize(),
                travelObservationPage.getTotalElements(), travelObservationPage.getTotalPages(), travelObservationPage.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public TravelObservationDTO getTravelObservationById(Long id) {
        TravelObservation travelObservation = travelObservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Travel observation not found"));
        return mapToDTO(travelObservation);
    }

    @Override
    @Transactional
    public TravelObservationDTO createTravelObservation(TravelObservationDTO travelObservationDTO) {
        // Validar si la orden de viaje existe
        TravelOrder travelOrder = travelOrderRepository.findById(travelObservationDTO.getTravelOrderId())
                .orElseThrow(() -> new RuntimeException("Travel order not found"));

        TravelObservation travelObservation = mapToEntity(travelObservationDTO, travelOrder);
        TravelObservation savedObservation = travelObservationRepository.save(travelObservation);
        return mapToDTO(savedObservation);
    }

    @Override
    @Transactional
    public void updateTravelObservation(Long id, TravelObservationDTO travelObservationDTO) {
        TravelObservation travelObservation = travelObservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Travel observation not found"));

        travelObservation.setObservationDesc(travelObservationDTO.getObservationDesc());
        travelObservation.setDriver(travelObservationDTO.getDriver());

        // Validar y actualizar la orden de viaje
        TravelOrder travelOrder = travelOrderRepository.findById(travelObservationDTO.getTravelOrderId())
                .orElseThrow(() -> new RuntimeException("Travel order not found"));
        travelObservation.setTravelOrderID(travelOrder);

        travelObservationRepository.save(travelObservation);
    }

    @Override
    @Transactional
    public void deleteTravelObservation(Long id) {
        travelObservationRepository.deleteById(id);
    }

    // NUEVO: Buscar observaciones por driver_id
//    @Transactional(readOnly = true)
//    public PageResponse<TravelObservationDTO> getObservationsByDriver(Long driverId, int pageNumber, int pageSize) {
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//        Page<TravelObservation> observationsPage = travelObservationRepository.findByDriver_Id(driverId, pageable);
//
//        List<TravelObservationDTO> dtos = observationsPage.stream().map(this::mapToDTO).toList();
//        return PageResponse.of(dtos, observationsPage.getNumber(), observationsPage.getSize(),
//                observationsPage.getTotalElements(), observationsPage.getTotalPages(), observationsPage.isLast());
//    }

    // Mapear desde el modelo a DTO
    private TravelObservationDTO mapToDTO(TravelObservation travelObservation) {
        return TravelObservationDTO.builder()
                .observationId(travelObservation.getObservationId())
                .observationDesc(travelObservation.getObservationDesc())
                .travelOrderId(travelObservation.getTravelOrderID().getTravelOrderId())
                .driver(travelObservation.getDriver())
                .build();
    }

    // Mapear desde DTO al modelo (incluyendo la orden de viaje)
    private TravelObservation mapToEntity(TravelObservationDTO travelObservationDTO, TravelOrder travelOrder) {
        return TravelObservation.builder()
                .observationId(travelObservationDTO.getObservationId())
                .observationDesc(travelObservationDTO.getObservationDesc())
                .travelOrderID(travelOrder)
                .driver(travelObservationDTO.getDriver())
                .build();
    }
}
