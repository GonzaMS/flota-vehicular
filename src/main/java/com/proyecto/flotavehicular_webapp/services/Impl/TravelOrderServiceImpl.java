package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.TravelOrderDTO;
import com.proyecto.flotavehicular_webapp.models.TravelOrder;
import com.proyecto.flotavehicular_webapp.repositories.ITravelOrderRepository;
import com.proyecto.flotavehicular_webapp.services.ITravelOrderService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TravelOrderServiceImpl implements ITravelOrderService {

    @Autowired
    private ITravelOrderRepository travelOrderRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TravelOrderDTO> getAllTravelOrders(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<TravelOrder> travelOrderPage = travelOrderRepository.findAll(pageable);

        List<TravelOrderDTO> dtos = travelOrderPage.stream().map(this::mapToDTO).toList();
        return PageResponse.of(dtos, travelOrderPage.getNumber(), travelOrderPage.getSize(),
                travelOrderPage.getTotalElements(), travelOrderPage.getTotalPages(), travelOrderPage.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public TravelOrderDTO getTravelOrderById(Long id) {
        TravelOrder travelOrder = travelOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Travel order not found"));
        return mapToDTO(travelOrder);
    }

    @Override
    @Transactional
    public TravelOrderDTO createTravelOrder(TravelOrderDTO travelOrderDTO) {
        TravelOrder travelOrder = mapToEntity(travelOrderDTO);
        TravelOrder savedOrder = travelOrderRepository.save(travelOrder);
        return mapToDTO(savedOrder);
    }

    @Override
    @Transactional
    public void updateTravelOrder(Long id, TravelOrderDTO travelOrderDTO) {
        TravelOrder travelOrder = travelOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Travel order not found"));
        travelOrder.setClient(travelOrderDTO.getClient());
        travelOrder.setTravelLeaveDate(travelOrderDTO.getTravelLeaveDate());
        travelOrder.setTravelArriveDate(travelOrderDTO.getTravelArriveDate());
        travelOrder.setTravelOrderState(travelOrderDTO.getTravelOrderState());
        travelOrderRepository.save(travelOrder);
    }

    @Override
    @Transactional
    public void deleteTravelOrder(Long id) {
        travelOrderRepository.deleteById(id);
    }

    private TravelOrderDTO mapToDTO(TravelOrder travelOrder) {
        return TravelOrderDTO.builder()
                .travelOrderId(travelOrder.getTravelOrderId())
                .client(travelOrder.getClient())
                .travelLeaveDate(travelOrder.getTravelLeaveDate())
                .travelArriveDate(travelOrder.getTravelArriveDate())
                .travelOrderState(travelOrder.getTravelOrderState())
                .build();
    }

    private TravelOrder mapToEntity(TravelOrderDTO travelOrderDTO) {
        return TravelOrder.builder()
                .travelOrderId(travelOrderDTO.getTravelOrderId())
                .client(travelOrderDTO.getClient())
                .travelLeaveDate(travelOrderDTO.getTravelLeaveDate())
                .travelArriveDate(travelOrderDTO.getTravelArriveDate())
                .travelOrderState(travelOrderDTO.getTravelOrderState())
                .build();
    }
}
