package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.CarIncidentsDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.models.CarIncidents;
import com.proyecto.flotavehicular_webapp.repositories.ICarIncidentsRepository;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.services.ICarIncidentsService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ICarIncidentsServiceImpl implements ICarIncidentsService {

    private final ICarIncidentsRepository carIncidentsRepository;

    private final ICarRepository carRepository;

    private static final String NOTFOUND = "CarIncidents not found";

    private static final Logger logger = LoggerFactory.getLogger(ICarIncidentsServiceImpl.class);

    public ICarIncidentsServiceImpl(ICarIncidentsRepository incidentRepository, ICarRepository carRepository) {
        this.carIncidentsRepository = incidentRepository;
        this.carRepository = carRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CarIncidentsDTO> getAll(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<CarIncidents> incidentPage = carIncidentsRepository.findAll(pageable);

            return mapToPageResponse(incidentPage);

        } catch (Exception e) {
            logger.error("Error getting all incidents: {}", e.getMessage());
            throw new ServiceException("Error getting all incidents");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "carIncidents", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('carIncidents', #id)")
    public CarIncidentsDTO getById(Long id) {
        CarIncidents carIncidents = carIncidentsRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(carIncidents);
    }

    @Override
    @Transactional
    @CachePut(value = "carIncidents", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('carIncidents', #carIncidentsDTO.getIncidentId())")
    public CarIncidents save(CarIncidentsDTO carIncidentsDTO) {
        try {
            Car car = carRepository.findById(carIncidentsDTO.getCarId()).orElseThrow(() -> new NotFoundException("Car not found"));

            CarIncidents carIncidents = mapToEntity(carIncidentsDTO);
            carIncidents.setCar(car);

            return carIncidentsRepository.save(carIncidents);
        } catch (NotFoundException e) {
            logger.warn("Car with id {} not found", carIncidentsDTO.getCarId());
            throw e;
        } catch (Exception e) {
            logger.error("Error saving incident: {}", e.getMessage());
            throw new ServiceException("Error saving incident");
        }
    }

    @Override
    @Transactional
    public void update(Long id, CarIncidentsDTO carIncidentsDTO) {
        try {
            CarIncidents carIncidents = carIncidentsRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));

            carIncidents.setIncidentDate(carIncidentsDTO.getIncidentDate());
            carIncidents.setIncidentDescription(carIncidentsDTO.getIncidentDescription());
            carIncidents.setIncidentType(carIncidentsDTO.getIncidentType());

            carIncidentsRepository.save(carIncidents);
        } catch (NotFoundException e) {
            logger.warn("Incident with id {} not found", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating incident: {}", e.getMessage());
            throw new ServiceException("Error updating incident");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "carIncidents", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('carIncidents', #id)")
    public void delete(Long id) {
        try {
            CarIncidents carIncidents = carIncidentsRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
            carIncidentsRepository.delete(carIncidents);

        } catch (NotFoundException e) {
            logger.warn("Incident with id {} not found", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting incident: {}", e.getMessage());
            throw new ServiceException("Error deleting incident");
        }
    }

    // Filters
    @Override
    public PageResponse<CarIncidentsDTO> getByCarId(Long id, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<CarIncidents> carIncidentsPage = carIncidentsRepository.findByCar_CarId(id, pageable);

            if (carIncidentsPage.isEmpty()) {
                throw new NotFoundException(NOTFOUND + " for car with id: " + id);
            }

            return mapToPageResponse(carIncidentsPage);
        } catch (NotFoundException e) {
            logger.warn("Incidents not found for car with id: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error getting incidents by car id: {}", e.getMessage());
            throw new ServiceException("Error getting incidents by car id");
        }
    }

    // Mappers
    // Map Entity to DTO
    private CarIncidentsDTO mapToDTO(CarIncidents carIncidents) {
        return CarIncidentsDTO.builder()
                .incidentId(carIncidents.getIncidentId())
                .incidentDate(carIncidents.getIncidentDate())
                .incidentDescription(carIncidents.getIncidentDescription())
                .incidentType(carIncidents.getIncidentType())
                .build();
    }

    // Map DTO to Entity
    private CarIncidents mapToEntity(CarIncidentsDTO carIncidentsDTO) {
        return CarIncidents.builder()
                .incidentId(carIncidentsDTO.getIncidentId())
                .incidentDate(carIncidentsDTO.getIncidentDate())
                .incidentDescription(carIncidentsDTO.getIncidentDescription())
                .incidentType(carIncidentsDTO.getIncidentType())
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
