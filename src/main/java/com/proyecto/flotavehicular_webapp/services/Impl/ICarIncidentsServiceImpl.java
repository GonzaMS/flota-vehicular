package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.car.CarIncidentsDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car.Car;
import com.proyecto.flotavehicular_webapp.models.Car.CarIncidents;
import com.proyecto.flotavehicular_webapp.repositories.ICarIncidentsRepository;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.services.ICarIncidentsService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ICarIncidentsServiceImpl implements ICarIncidentsService {

    private final ICarIncidentsRepository carIncidentsRepository;
    private final CacheManager cacheManager;
    private final ICarRepository carRepository;

    private static final String NOTFOUND = "CarIncidents not found";

    public ICarIncidentsServiceImpl(ICarIncidentsRepository incidentRepository, ICarRepository carRepository, CacheManager cacheManager) {
        this.carIncidentsRepository = incidentRepository;
        this.carRepository = carRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResponse<CarIncidentsDTO> getAll(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<CarIncidents> incidentPage = carIncidentsRepository.findAll(pageable);

            incidentPage.forEach(incidents -> {
                String key = RedisUtils.CacheKeyGenerator("api_incidents_", incidents.getId());
                Cache cache = cacheManager.getCache(key);

                if (cache != null) {
                    Object incidentsOnCache = cache.get(key, Object.class);
                    if (incidentsOnCache == null) {
                        cache.put(key, incidents);
                    }
                }
            });
            return mapToPageResponse(incidentPage);
        } catch (Exception e) {
            log.error("Error getting all incidents: {}", e.getMessage());
            throw new ServiceException("Error getting all incidents");
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Cacheable(value = "carIncidents", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('carIncidents', #id)")
    public CarIncidentsDTO getById(Long id) {
        CarIncidents carIncidents = carIncidentsRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(carIncidents);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 10)
    public CarIncidents save(CarIncidentsDTO carIncidentsDTO) {
        try {
            Car car = carRepository.findById(carIncidentsDTO.getCarId()).orElseThrow(() -> new NotFoundException("Car not found"));

            CarIncidents carIncidents = mapToEntity(carIncidentsDTO);
            carIncidents.setCar(car);
            carIncidents.setCreatedAt(new Date());

            return carIncidentsRepository.save(carIncidents);
        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: carIncidentsDTO={}, carId={}", carIncidentsDTO, carIncidentsDTO.getCarId());
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error saving incident: {}, Parameters: carIncidentsDTO={}", e.getMessage(), carIncidentsDTO);
            throw new ServiceException("Error saving incident");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 10)
    @CachePut(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_carIncidents_', #id)")
    public CarIncidentsDTO update(Long id, CarIncidentsDTO carIncidentsDTO) {
        try {
            CarIncidents carIncidents = carIncidentsRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));

            carIncidents.setDescription(carIncidentsDTO.getDescription());
            carIncidents.setType(carIncidentsDTO.getType());
            carIncidents.setUpdatedAt(new Date());

            carIncidentsRepository.save(carIncidents);

            return mapToDTO(carIncidents);
        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: id={}, carIncidentsDTO={}", id, carIncidentsDTO);
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error updating incident: {}, Parameters: id={}, carIncidentsDTO={}", e.getMessage(), id, carIncidentsDTO);
            throw new ServiceException("Error updating incident");
        }
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = Exception.class, timeout = 10)
    @CacheEvict(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_carIncidents_', #id)")
    public void delete(Long id) {
        try {
            CarIncidents carIncidents = carIncidentsRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
            carIncidentsRepository.delete(carIncidents);
            String key = RedisUtils.CacheKeyGenerator("api_carIncidents_", id);
            Cache cache = cacheManager.getCache("sd");
            if (cache != null) {
                cache.evict(key);
            }

        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: id={}", id);
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error deleting incident: {}, Parameters: id={}", e.getMessage(), id);
            throw new ServiceException("Error deleting incident");
        }
    }

    // Filters
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, timeout = 5)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_carIncidents_', #id)")
    public PageResponse<CarIncidentsDTO> getByCarId(Long id, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<CarIncidents> carIncidentsPage = carIncidentsRepository.findByCarId(id, pageable);

            if (carIncidentsPage.isEmpty()) {
                throw new NotFoundException(NOTFOUND + " for car with id: " + id);
            }

            return mapToPageResponse(carIncidentsPage);
        } catch (NotFoundException e) {
            log.error("Incidents not found for car with id: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error getting incidents by car id: {}", e.getMessage());
            throw new ServiceException("Error getting incidents by car id");
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, timeout = 5)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_carIncidents_', #startDate, #endDate)")
    public PageResponse<CarIncidentsDTO> getByDate(Date startDate, Date endDate, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<CarIncidents> carIncidentsPage = carIncidentsRepository.findByCreatedAtBetween(startDate, endDate, pageable);

            if (carIncidentsPage.isEmpty()) {
                throw new NotFoundException(NOTFOUND + " between " + startDate + " and " + endDate);
            }

            return mapToPageResponse(carIncidentsPage);
        } catch (NotFoundException e) {
            log.error("Incidents not found between {} and {}", startDate, endDate);
            throw e;
        } catch (Exception e) {
            log.error("Error getting incidents by created at: {}", e.getMessage());
            throw new ServiceException("Error getting incidents by created at");
        }
    }

    // Mappers
// Map Entity to DTO
    private CarIncidentsDTO mapToDTO(CarIncidents carIncidents) {
        return CarIncidentsDTO.builder()
                .id(carIncidents.getId())
                .createdAt(carIncidents.getCreatedAt())
                .updatedAt(carIncidents.getUpdatedAt())
                .description(carIncidents.getDescription())
                .type(carIncidents.getType())
                .build();
    }

    // Map DTO to Entity
    private CarIncidents mapToEntity(CarIncidentsDTO carIncidentsDTO) {
        return CarIncidents.builder()
                .id(carIncidentsDTO.getId())
                .createdAt(carIncidentsDTO.getCreatedAt())
                .updatedAt(carIncidentsDTO.getUpdatedAt())
                .description(carIncidentsDTO.getDescription())
                .type(carIncidentsDTO.getType())
                .build();
    }

    private PageResponse<CarIncidentsDTO> mapToPageResponse(Page<CarIncidents> carIncidentsPage) {
        List<CarIncidentsDTO> carIncidentsDTOList = carIncidentsPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                carIncidentsDTOList,
                carIncidentsPage.getNumber(),
                carIncidentsPage.getSize(),
                carIncidentsPage.getTotalElements(),
                carIncidentsPage.getTotalPages(),
                carIncidentsPage.isLast());
    }
}
