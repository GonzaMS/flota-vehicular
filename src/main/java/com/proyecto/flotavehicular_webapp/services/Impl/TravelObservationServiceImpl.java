package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.TravelObservationDTO;
import com.proyecto.flotavehicular_webapp.models.TravelObservation;
import com.proyecto.flotavehicular_webapp.models.TravelOrder;
import com.proyecto.flotavehicular_webapp.repositories.ITravelObservationRepository;
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

        TravelOrder travelOrder = travelObservationDTO.getTravelOrderId();

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

        // Actualizar el objeto TravelOrder completo
        travelObservation.setTravelOrderID(travelObservationDTO.getTravelOrderId());

        travelObservationRepository.save(travelObservation);
    }

    @Override
    @Transactional
    public void deleteTravelObservation(Long id) {
        travelObservationRepository.deleteById(id);
    }

    // Mapear desde el modelo a DTO
    private TravelObservationDTO mapToDTO(TravelObservation travelObservation) {
        return TravelObservationDTO.builder()
                .observationId(travelObservation.getObservationId())
                .observationDesc(travelObservation.getObservationDesc())
                .travelOrderId(travelObservation.getTravelOrderID())  // Pasamos el objeto completo
                .driver(travelObservation.getDriver())
                .build();
    }

    // Mapear desde DTO al modelo
    private TravelObservation mapToEntity(TravelObservationDTO travelObservationDTO, TravelOrder travelOrder) {
        return TravelObservation.builder()
                .observationId(travelObservationDTO.getObservationId())
                .observationDesc(travelObservationDTO.getObservationDesc())
                .travelOrderID(travelOrder)  // Usamos el objeto TravelOrder completo
                .driver(travelObservationDTO.getDriver())
                .build();
    }
}
