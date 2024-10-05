package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.DriverIncidentsDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Driver;
import com.proyecto.flotavehicular_webapp.models.DriverIncidents;
import com.proyecto.flotavehicular_webapp.repositories.IDriverIncidentsRepository;
import com.proyecto.flotavehicular_webapp.repositories.IDriverRepository;
import com.proyecto.flotavehicular_webapp.services.IDriverIncidentsService;
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
public class IDriverIncidentsServiceImpl implements IDriverIncidentsService {

    private final IDriverIncidentsRepository incidentRecordRepository;
    private final IDriverRepository driverRepository;
    private final CacheManager cacheManager;

    private static final String NOTFOUND = "Incident record not found";
    private static final String DRIVER_NOT_FOUND = "Driver not found";
    private static final Logger logger = LoggerFactory.getLogger(IDriverIncidentsServiceImpl.class);


    public IDriverIncidentsServiceImpl(IDriverIncidentsRepository incidentRecordRepository, IDriverRepository driverRepository, CacheManager cacheManager) {
        this.incidentRecordRepository = incidentRecordRepository;
        this.driverRepository = driverRepository;
        this.cacheManager = cacheManager;

    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DriverIncidentsDTO> getAllIncidentRecords(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<DriverIncidents> incidentRecordPage = incidentRecordRepository.findAll(pageable);

            incidentRecordPage.forEach(incidents -> {
                        String key = RedisUtils.CacheKeyGenerator("api_incidents_", incidents.getIncidentId());
                        Cache cache = cacheManager.getCache(key);

                        if (cache != null) {
                            Object incidentsOnCache = cache.get(key, Object.class);
                            if (incidentsOnCache == null) {
                                cache.put(key, incidents);
                            }
                        }
                    });
            return mapToPageResponse(incidentRecordPage);

        } catch (Exception e) {
            logger.error("Error getting all incidents: {}", e.getMessage());
            throw new ServiceException("Error getting all incidents");
        }

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "driverIncidents", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('driverIncidents', #id)")
    public DriverIncidentsDTO getIncidentRecordById(Long id) {
        DriverIncidents incidentRecord = incidentRecordRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(incidentRecord);
    }

    @Override
    @Transactional
    public DriverIncidents saveIncidentRecord(DriverIncidentsDTO driverIncidentsDTO) {
        try {
            Driver driver = driverRepository.findById(driverIncidentsDTO.getDriverId()).orElseThrow(() -> new NotFoundException(DRIVER_NOT_FOUND));

            DriverIncidents incidentRecord = mapToEntity(driverIncidentsDTO);
            incidentRecord.setDriver(driver);

            return incidentRecordRepository.save(incidentRecord);
        } catch (NotFoundException e) {
            logger.error("Driver with id {} not found", driverIncidentsDTO.getDriverId());
            throw e;
        } catch (Exception e) {
            logger.error("Error saving incident: {}", e.getMessage());
            throw new ServiceException("Error saving incident");
        }
    }

    @Override
    @Transactional
    @CachePut(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_driverIncidents_', #id)")
    public DriverIncidentsDTO updateIncidentRecord(Long id, DriverIncidentsDTO driverIncidentsDTO) {
        try {
            DriverIncidents incidentRecord = incidentRecordRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(NOTFOUND));

            Driver driver = driverRepository.findById(driverIncidentsDTO.getDriverId())
                    .orElseThrow(() -> new NotFoundException(DRIVER_NOT_FOUND));

            incidentRecord.setIncidentDescription(driverIncidentsDTO.getIncidentDescription());
            incidentRecord.setIncidentType(driverIncidentsDTO.getIncidentType());
            incidentRecord.setDriver(driver);

            incidentRecordRepository.save(incidentRecord);

            return mapToDTO(incidentRecord);

        } catch (NotFoundException e) {
            logger.error("Incident with id {} not found", id);
            throw e;
        } catch (Exception e){
            logger.error("Error updating incident: {}", e.getMessage());
            throw new ServiceException("Error updating incident");
        }

    }

    @Override
    @Transactional
    @CacheEvict(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_driverIncidents_', #id)")
    public void deleteIncidentRecord(Long id) {
        try {
            DriverIncidents driverIncidents = incidentRecordRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
            incidentRecordRepository.delete(driverIncidents);
            String Key = RedisUtils.CacheKeyGenerator("api_driverIncidents_", id);
            Cache cache = cacheManager.getCache("sd");
            if (cache != null){
                cache.evict(Key);
            }
        } catch (NotFoundException e) {
            logger.error("Incident with id {} not found", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting incident: {}", e.getMessage());
            throw new ServiceException("Error deleting incident");
        }
    }



    @Override
    @Transactional(readOnly = true)
    public PageResponse<DriverIncidentsDTO> getIncidentRecordsByDriver(Long driverId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<DriverIncidents> incidentRecordPage = incidentRecordRepository.findByDriver_DriverId(driverId, pageable);

        List<DriverIncidentsDTO> driverIncidentsDTOList = incidentRecordPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                driverIncidentsDTOList,
                incidentRecordPage.getNumber(),
                incidentRecordPage.getSize(),
                incidentRecordPage.getTotalElements(),
                incidentRecordPage.getTotalPages(),
                incidentRecordPage.isLast());
    }

    private DriverIncidents mapToEntity(DriverIncidentsDTO driverIncidentsDTO) {
        return DriverIncidents.builder()
                .incidentId(driverIncidentsDTO.getIncidentId())
                .incidentDescription(driverIncidentsDTO.getIncidentDescription())
                .createdAt(driverIncidentsDTO.getCreatedAt())
                .incidentType(driverIncidentsDTO.getIncidentType())
                .build();
    }

    private DriverIncidentsDTO mapToDTO(DriverIncidents incidentRecord) {
        return DriverIncidentsDTO.builder()
                .incidentId(incidentRecord.getIncidentId())
                .incidentDescription(incidentRecord.getIncidentDescription())
                .createdAt(incidentRecord.getCreatedAt())
                .incidentType(incidentRecord.getIncidentType())
                .driverId(incidentRecord.getDriver().getDriverId())
                .build();
    }

    private PageResponse<DriverIncidentsDTO> mapToPageResponse(Page<DriverIncidents> driverIncidents) {
        List<DriverIncidentsDTO> carIncidentsDTOList = driverIncidents.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                carIncidentsDTOList,
                driverIncidents.getNumber(),
                driverIncidents.getSize(),
                driverIncidents.getTotalElements(),
                driverIncidents.getTotalPages(),
                driverIncidents.isLast());
    }
}
