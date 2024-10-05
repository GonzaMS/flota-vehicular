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
import com.proyecto.flotavehicular_webapp.utils.RedisUtils;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    private final CacheManager cacheManager;


    private static final String NOT_FOUND = "Assigned order not found";
    private static final String DRIVER_NOT_FOUND = "Driver not found";

    private static final Logger logger = LoggerFactory.getLogger(IAssignedOrderServiceImpl.class);


    private final ExternalApiService externalApiService;

    public IAssignedOrderServiceImpl(IAssignedOrderRepository assignedOrderRepository,
                                         IDriverRepository driverRepository,
                                         ExternalApiService externalApiService,
                                        CacheManager cacheManager) {
            this.assignedOrderRepository = assignedOrderRepository;
            this.driverRepository = driverRepository;
            this.externalApiService = externalApiService;
            this.cacheManager = cacheManager;
    }


    @Override
    @Transactional(readOnly = true)
    public PageResponse<AssignedOrderDTO> getAllAssignedOrders(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<AssignedOrder> assignedOrderPage = assignedOrderRepository.findAll(pageable);

            assignedOrderPage.forEach(assignedOrder -> {
                String key = RedisUtils.CacheKeyGenerator("api_assignedOrder_", assignedOrder.getAssignedOrderId());
                Cache cache = cacheManager.getCache(key);

                if (cache != null) {
                    Object assignedOrderOnCache = cache.get(key, Object.class);
                    if (assignedOrderOnCache == null) {
                        cache.put(key, assignedOrder);
                    }
                }
            });

            return mapToPageResponse(assignedOrderPage);
        } catch (Exception e) {
            logger.error("Error getting all assigned orders: {}", e.getMessage());
            throw new ServiceException("Error getting all assigned orders");
        }
    }


    @Override
        @Transactional(readOnly = true)
    @Cacheable(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_assignedOrder_', #id)")
        public AssignedOrderDTO getAssignedOrderById(Long id) {
            AssignedOrder assignedOrder = assignedOrderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(NOT_FOUND));
            return mapToDTO(assignedOrder);
        }

        @Override
        @Transactional
        public AssignedOrder saveAssignedOrder(AssignedOrderDTO assignedOrderDTO) {
            try {
                Driver driver = driverRepository.findById(assignedOrderDTO.getDriverId())
                        .orElseThrow(() -> new NotFoundException(DRIVER_NOT_FOUND));
                Car car = externalApiService.callExternalApi(assignedOrderDTO.getCarId());
                if (car == null){
                    throw new NotFoundException("Car not found");
                }

                AssignedOrder assignedOrder = mapToEntity(assignedOrderDTO);
                assignedOrder.setDriver(driver);
                assignedOrder.setCar(car);

                return assignedOrderRepository.save(assignedOrder);
            } catch (NotFoundException e) {
                logger.error("Car with id {} not found", assignedOrderDTO.getCarId());
                throw e;
            } catch (Exception e) {
                logger.error("Error saving assigned orders: {}", e.getMessage());
                throw new ServiceException("Error saving assigned orders");
            }

        }

        @Override
        @Transactional
        @CachePut(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_assignedOrder_', #id)")
        public AssignedOrderDTO updateAssignedOrder(Long id, AssignedOrderDTO assignedOrderDTO) {
            try {
                AssignedOrder assignedOrder = assignedOrderRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(NOT_FOUND));

                Driver driver = driverRepository.findById(assignedOrderDTO.getDriverId())
                        .orElseThrow(() -> new NotFoundException(DRIVER_NOT_FOUND));
                Car car = externalApiService.callExternalApi(assignedOrderDTO.getCarId());
                if (car == null){
                    throw new NotFoundException("Car not found");
                }
                assignedOrder.setItinerary(assignedOrderDTO.getItinerary());
                assignedOrder.setDriver(driver);
                assignedOrder.setCar(car);
                assignedOrder.setTravelOrderId(assignedOrderDTO.getTravelOrderId());
                assignedOrderRepository.save(assignedOrder);

                return mapToDTO(assignedOrder);
            }   catch (NotFoundException e) {
                logger.error("assigned orders with id {} not found", id);
                throw e;
            } catch (Exception e) {
                logger.error("Error updating assigned orders: {}", e.getMessage());
                throw new ServiceException("Error updating assigned orders");
            }
        }

        @Override
        @Transactional
        @CacheEvict(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_assignedOrder_', #id)")

        public void deleteAssignedOrder(Long id) {
            AssignedOrder assignedOrder = assignedOrderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(NOT_FOUND));
            assignedOrderRepository.delete(assignedOrder);
        }

    private AssignedOrder mapToEntity(AssignedOrderDTO assignedOrderDTO) {
        return AssignedOrder.builder()
                .assignedOrderId(assignedOrderDTO.getAssignedOrderId())
                .createdAt(assignedOrderDTO.getCreatedAt())
                .itinerary(assignedOrderDTO.getItinerary())
                .travelOrderId(assignedOrderDTO.getTravelOrderId())
                .build();
    }

        private AssignedOrderDTO mapToDTO(AssignedOrder assignedOrder) {
            return AssignedOrderDTO.builder()
                    .assignedOrderId(assignedOrder.getAssignedOrderId())
                    .createdAt(assignedOrder.getCreatedAt())
                    .itinerary(assignedOrder.getItinerary())
                    .driverId(assignedOrder.getDriver().getDriverId())
                    .carId(assignedOrder.getCar().getId())
                    .travelOrderId(assignedOrder.getTravelOrderId())
                    .build();
        }


    // Page Response
    private PageResponse<AssignedOrderDTO> mapToPageResponse(Page<AssignedOrder> assignedOrderPage ) {
        List<AssignedOrderDTO> AssignedOrderDTO = assignedOrderPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                AssignedOrderDTO,
                assignedOrderPage.getNumber(),
                assignedOrderPage.getSize(),
                assignedOrderPage.getTotalElements(),
                assignedOrderPage.getTotalPages(),
                assignedOrderPage.isLast());
    }
}

