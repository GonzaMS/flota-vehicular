package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.AssignedOrderDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.AssignedOrder;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.models.Driver;
import com.proyecto.flotavehicular_webapp.repositories.IAssignedOrderRepository;
import com.proyecto.flotavehicular_webapp.repositories.IDriverRepository;
import com.proyecto.flotavehicular_webapp.services.ExternalApiService;
import com.proyecto.flotavehicular_webapp.services.IAssignedOrderService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IAssignedOrderServiceImpl implements IAssignedOrderService{

        private final IAssignedOrderRepository assignedOrderRepository;
        private final IDriverRepository driverRepository;

        private static final String NOT_FOUND = "Assigned order not found";
        private static final String DRIVER_NOT_FOUND = "Driver not found";
    private final ExternalApiService externalApiService;

    public IAssignedOrderServiceImpl(IAssignedOrderRepository assignedOrderRepository,
                                         IDriverRepository driverRepository,
                                         ExternalApiService externalApiService) {
            this.assignedOrderRepository = assignedOrderRepository;
            this.driverRepository = driverRepository;
        this.externalApiService = externalApiService;
    }

        @Override
        @Transactional(readOnly = true)
        public AssignedOrderDTO getAssignedOrderById(Long id) {
            AssignedOrder assignedOrder = assignedOrderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(NOT_FOUND));
            return mapToDTO(assignedOrder);
        }

        @Override
        @Transactional
        public AssignedOrder saveAssignedOrder(AssignedOrderDTO assignedOrderDTO) {
            Driver driver = driverRepository.findById(assignedOrderDTO.getDriverId())
                    .orElseThrow(() -> new NotFoundException(DRIVER_NOT_FOUND));
            Car car = externalApiService.callExternalApi(assignedOrderDTO.getCarId());
            if (car == null){
                throw new NotFoundException("Car not found");
            }

            // Agrega un log para verificar el valor de travelOrderId
            System.out.println("TravelOrderId recibido: " + assignedOrderDTO.getTravelOrderId());

            AssignedOrder assignedOrder = mapToEntity(assignedOrderDTO);
            assignedOrder.setDriver(driver);
            assignedOrder.setCar(car);
            return assignedOrderRepository.save(assignedOrder);
        }

        @Override
        @Transactional
        public void updateAssignedOrder(Long id, AssignedOrderDTO assignedOrderDTO) {
            AssignedOrder assignedOrder = assignedOrderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(NOT_FOUND));

            Driver driver = driverRepository.findById(assignedOrderDTO.getDriverId())
                    .orElseThrow(() -> new NotFoundException(DRIVER_NOT_FOUND));
            Car car = externalApiService.callExternalApi(assignedOrderDTO.getCarId());
            if (car == null){
                throw new NotFoundException("Car not found");
            }
            assignedOrder.setAssignedDate(assignedOrderDTO.getAssignedDate());
            assignedOrder.setItinerary(assignedOrderDTO.getItinerary());
            assignedOrder.setDriver(driver);
            assignedOrder.setCar(car);
            assignedOrder.setTravelOrderId(assignedOrderDTO.getTravelOrderId());
            assignedOrderRepository.save(assignedOrder);
        }

        @Override
        @Transactional
        public void deleteAssignedOrder(Long id) {
            AssignedOrder assignedOrder = assignedOrderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(NOT_FOUND));
            assignedOrderRepository.delete(assignedOrder);
        }

        @Override
        @Transactional(readOnly = true)
        public PageResponse<AssignedOrderDTO> getAllAssignedOrders(int pageNumber, int pageSize) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<AssignedOrder> assignedOrderPage = assignedOrderRepository.findAll(pageable);

            List<AssignedOrderDTO> assignedOrderDTOList = assignedOrderPage.stream()
                    .map(this::mapToDTO)
                    .toList();

            return PageResponse.of(
                    assignedOrderDTOList,
                    assignedOrderPage.getNumber(),
                    assignedOrderPage.getSize(),
                    assignedOrderPage.getTotalElements(),
                    assignedOrderPage.getTotalPages(),
                    assignedOrderPage.isLast());
        }

    private AssignedOrder mapToEntity(AssignedOrderDTO assignedOrderDTO) {
        return AssignedOrder.builder()
                .assignedOrderId(assignedOrderDTO.getAssignedOrderId())
                .assignedDate(assignedOrderDTO.getAssignedDate())
                .itinerary(assignedOrderDTO.getItinerary())
                .travelOrderId(assignedOrderDTO.getTravelOrderId())
                .build();
    }

        private AssignedOrderDTO mapToDTO(AssignedOrder assignedOrder) {
            return AssignedOrderDTO.builder()
                    .assignedOrderId(assignedOrder.getAssignedOrderId())
                    .assignedDate(assignedOrder.getAssignedDate())
                    .itinerary(assignedOrder.getItinerary())
                    .driverId(assignedOrder.getDriver().getDriverId())
                    .carId(assignedOrder.getCar().getCarId())
                    .travelOrderId(assignedOrder.getTravelOrderId())
                    .build();
        }
}

