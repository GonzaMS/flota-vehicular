package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.car.KilometersDTO;
import com.proyecto.flotavehicular_webapp.dto.driver.DrivingHistoryDTO;
import com.proyecto.flotavehicular_webapp.dto.travel.TravelOrderDTO;
import com.proyecto.flotavehicular_webapp.enums.EORDERSSTATE;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car.Kilometers;
import com.proyecto.flotavehicular_webapp.models.Driver.DrivingHistory;
import com.proyecto.flotavehicular_webapp.models.Travel.AssignedOrder;
import com.proyecto.flotavehicular_webapp.models.Travel.TravelOrder;
import com.proyecto.flotavehicular_webapp.repositories.ITravelOrderRepository;
import com.proyecto.flotavehicular_webapp.services.ITravelOrderService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ITravelOrderServiceImpl implements ITravelOrderService {

    private final ITravelOrderRepository travelOrderRepository;
    private final CacheManager cacheManager;
    private final ExternalServiceApi externalServiceApi;

    private static final String NOT_FOUND = "Travel order not found";


    public ITravelOrderServiceImpl(ITravelOrderRepository travelOrderRepository, CacheManager cacheManager, ExternalServiceApi externalServiceApi) {
        this.travelOrderRepository = travelOrderRepository;
        this.cacheManager = cacheManager;
        this.externalServiceApi = externalServiceApi;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResponse<TravelOrderDTO> getAll(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<TravelOrder> travelOrderPage = travelOrderRepository.findAll(pageable);
            travelOrderPage.forEach(travelOrder -> {
                String key = RedisUtils.CacheKeyGenerator("api_travel_order", travelOrder.getTravelOrderId());
                Cache cache = cacheManager.getCache(key);

                if (cache != null) {
                    Object maintenanceOnCache = cache.get(key, Object.class);
                    if (maintenanceOnCache == null) {
                        cache.put(key, travelOrder);
                    }
                }
            });

            return mapToPageResponse(travelOrderPage);
        } catch (Exception e) {
            log.error("Error getting all travel orders: {}", e.getMessage());
            throw new ServiceException(NOT_FOUND);
        }

    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Cacheable(value = "sd", cacheManager = "cacheManagerWithoutTtl", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_travel_order', #id)")
    public TravelOrderDTO getById(Long id) {
        TravelOrder travelOrder = travelOrderRepository.findById(id)
                .orElseThrow(() -> new ServiceException(NOT_FOUND));

        return mapToDto(travelOrder);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 5)
    public TravelOrder save(TravelOrderDTO travelOrderDTO) {
        AssignedOrder assignedOrder = externalServiceApi.callExternalApi(travelOrderDTO.getAssignedOrderIds());

        DrivingHistory drivingHistory = externalServiceApi.callExternalApiDrivingHistory(travelOrderDTO.getAssignedOrderIds());

        if (drivingHistory == null) {
            externalServiceApi.callExternalApiCreateDrivingHistory(DrivingHistoryDTO.builder()
                    .kmDriven(travelOrderDTO.getKilometers())
                    .assignedOrderId(assignedOrder.getAssignedOrderId())
                    .build()
            );
        }

//        drivingHistory.setKmDriven(drivingHistory.getKmDriven() + travelOrderDTO.getKilometers());
//        drivingHistory.setAssignedOrder(assignedOrder);

        Kilometers kilometers = externalServiceApi.callExternalApiKilometers(assignedOrder.getCar().getId());


        if (kilometers == null) {
            externalServiceApi.callExternalApiCreateKilometers(KilometersDTO.builder()
                    .carId(assignedOrder.getCar().getId())
                    .actualKm(travelOrderDTO.getKilometers())
                    .build()
            );
        }

        TravelOrder travelOrder = mapToEntity(travelOrderDTO);
        travelOrder.setAssignedOrders(assignedOrder);

//        kilometers.setActualKm(kilometers.getActualKm() + travelOrder.getKilometers());

        return travelOrderRepository.save(travelOrder);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 10)
    @CachePut(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_travel_order', #id)")
    public TravelOrderDTO update(Long id, TravelOrderDTO travelOrderDTO) {
        try {

            TravelOrder travelOrder = travelOrderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(NOT_FOUND));

//            AssignedOrder assignedOrder = externalServiceApi.callExternalApi(travelOrderDTO.getAssignedOrderIds());

//            Kilometers kilometers = externalServiceApi.callExternalApiKilometers(assignedOrder.getCar().getId());

            travelOrder.setTravelLeaveDate(travelOrderDTO.getTravelLeaveDate());
            travelOrder.setTravelOrderState(travelOrderDTO.getTravelOrderState());
            travelOrder.setLatitudeOrigen(travelOrderDTO.getLatitudeOrigen());
            travelOrder.setLongitudeOrigen(travelOrderDTO.getLongitudeOrigen());
            travelOrder.setLatitudeDestination(travelOrderDTO.getLatitudeDestination());
            travelOrder.setLongitudeDestination(travelOrderDTO.getLongitudeDestination());
            travelOrder.setKilometers(travelOrderDTO.getKilometers());
//            travelOrder.setAssignedOrders(assignedOrder);

//            kilometers.setActualKm(kilometers.getActualKm() + travelOrder.getKilometers());

            travelOrderRepository.save(travelOrder);

            return mapToDto(travelOrder);
        } catch (NotFoundException e) {
            log.error("Error updating travel order: {}", e.getMessage());
            throw new ServiceException(NOT_FOUND);
        } catch (Exception e) {
            log.error("Error updating travel order: {}", e.getMessage());
            throw new ServiceException("Error updating travel order");
        }
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = Exception.class, timeout = 10)
    @CachePut(value = "sd", cacheManager = "cacheManagerWithoutTtl", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_travel_order', #id)")
    public void delete(Long id) {
        try {
            TravelOrder travelOrder = travelOrderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(NOT_FOUND));

            travelOrderRepository.delete(travelOrder);

        } catch (NotFoundException e) {
            log.error("Error deleting travel order: {}", e.getMessage());
            throw new ServiceException(NOT_FOUND);
        } catch (Exception e) {
            log.error("Error deleting travel order: {}", e.getMessage());
            throw new ServiceException("Error deleting travel order");
        }

    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = Exception.class, timeout = 10)
    @CachePut(value = "sd", cacheManager = "cacheManagerWithoutTtl", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_travel_order', #id)")
    public void cancelTravelOrder(Long id) {
        TravelOrder travelOrder = travelOrderRepository.findById(id)
                .orElseThrow(() -> new ServiceException(NOT_FOUND));

        travelOrder.setTravelOrderState(EORDERSSTATE.CANCEL);

        travelOrderRepository.save(travelOrder);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, timeout = 5)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_travel_order', #driverId)")
    public PageResponse<TravelOrderDTO> getByDriverId(Long driverId, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<TravelOrder> travelOrderPage = travelOrderRepository.findByDriverId(driverId, pageable);

            if (travelOrderPage.isEmpty()) {
                throw new NotFoundException(NOT_FOUND + " for driver id: " + driverId);
            }
            return mapToPageResponse(travelOrderPage);

        } catch (NotFoundException e) {
            log.error("Error getting travel order by driver id: {}", e.getMessage());
            throw new ServiceException(NOT_FOUND);
        } catch (Exception e) {
            log.error("Error getting travel order by driver id: {}", e.getMessage());
            throw new ServiceException("Error getting travel order by driver id");
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, timeout = 5)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_travel_order', #carId)")
    public PageResponse<TravelOrderDTO> getByCarId(Long carId, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<TravelOrder> travelOrderPage = travelOrderRepository.findByCarId(carId, pageable);

            if (travelOrderPage.isEmpty()) {
                throw new NotFoundException(NOT_FOUND + " for car id: " + carId);
            }
            return mapToPageResponse(travelOrderPage);

        } catch (NotFoundException e) {
            log.error("Error getting travel order by car id: {}", e.getMessage());
            throw new ServiceException(NOT_FOUND);
        } catch (Exception e) {
            log.error("Error getting travel order by car id: {}", e.getMessage());
            throw new ServiceException("Error getting travel order by car id");
        }
    }


    // Mappers
    // Map to dto
    private TravelOrderDTO mapToDto(TravelOrder travelOrder) {
        return TravelOrderDTO.builder()
                .travelOrderId(travelOrder.getTravelOrderId())
                .travelLeaveDate(travelOrder.getTravelLeaveDate())
                .travelOrderState(travelOrder.getTravelOrderState())
                .latitudeOrigen(travelOrder.getLatitudeOrigen())
                .longitudeOrigen(travelOrder.getLongitudeOrigen())
                .latitudeDestination(travelOrder.getLatitudeDestination())
                .longitudeDestination(travelOrder.getLongitudeDestination())
                .kilometers(travelOrder.getKilometers())
                .assignedOrderIds(travelOrder.getAssignedOrders().getAssignedOrderId())
                .build();
    }

    // Map to entity
    private TravelOrder mapToEntity(TravelOrderDTO travelOrderDTO) {
        return TravelOrder.builder()
                .travelOrderId(travelOrderDTO.getTravelOrderId())
                .travelLeaveDate(travelOrderDTO.getTravelLeaveDate())
                .travelOrderState(travelOrderDTO.getTravelOrderState())
                .latitudeOrigen(travelOrderDTO.getLatitudeOrigen())
                .longitudeOrigen(travelOrderDTO.getLongitudeOrigen())
                .latitudeDestination(travelOrderDTO.getLatitudeDestination())
                .longitudeDestination(travelOrderDTO.getLongitudeDestination())
                .kilometers(travelOrderDTO.getKilometers())
                .assignedOrders(AssignedOrder.builder().assignedOrderId(travelOrderDTO.getAssignedOrderIds()).build())
                .build();
    }


    private PageResponse<TravelOrderDTO> mapToPageResponse(Page<TravelOrder> maintenanceHistoryPage) {
        List<TravelOrderDTO> maintenanceDTOList = maintenanceHistoryPage.stream()
                .map(this::mapToDto)
                .toList();

        return PageResponse.of(
                maintenanceDTOList,
                maintenanceHistoryPage.getNumber(),
                maintenanceHistoryPage.getSize(),
                maintenanceHistoryPage.getTotalElements(),
                maintenanceHistoryPage.getTotalPages(),
                maintenanceHistoryPage.isLast()
        );
    }
}
