package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.KilometersDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.models.Kilometers;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.repositories.IKilometersRepository;
import com.proyecto.flotavehicular_webapp.services.IKilometersService;
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
public class IKilometersServiceImpl implements IKilometersService {

    private final IKilometersRepository kilometersRepository;
    private final ICarRepository carRepository;
    private final CacheManager cacheManager;

    private static final String KILOMETERS_NOT_FOUND = "Kilometers not found";

    private static final Logger logger = LoggerFactory.getLogger(IKilometersServiceImpl.class);

    public IKilometersServiceImpl(IKilometersRepository kilometersRepository, ICarRepository carRepository, CacheManager cacheManager) {
        this.kilometersRepository = kilometersRepository;
        this.carRepository = carRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<KilometersDTO> getAll(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Kilometers> kilometersPage = kilometersRepository.findAll(pageable);

            kilometersPage.forEach(kilometers -> {
                String key = RedisUtils.CacheKeyGenerator("api_kilometers_", kilometers.getKilometersId());
                Cache cache = cacheManager.getCache(key);

                if (cache != null) {
                    Object kilometersOnCache = cache.get(key, Object.class);
                    if (kilometersOnCache == null) {
                        cache.put(key, kilometers);
                    }
                }
            });
            return mapToPageResponse(kilometersPage);
        } catch (Exception e) {
            logger.error("Error getting all kilometers: {}", e.getMessage());
            throw new ServiceException("Error getting all kilometers");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_kilometers_', #id)")
    public KilometersDTO getById(Long id) {
        Kilometers kilometers = kilometersRepository.findById(id).orElseThrow(() -> new NotFoundException(KILOMETERS_NOT_FOUND));
        return mapToDTO(kilometers);
    }

    @Override
    @Transactional
    public Kilometers save(KilometersDTO kilometersDTO) {
        try {
            Car car = carRepository.findById(kilometersDTO.getCarId()).orElseThrow(() -> new NotFoundException("Car not found"));

            Kilometers kilometers = mapToEntity(kilometersDTO);
            kilometers.setCar(car);

            return kilometersRepository.save(kilometers);
        } catch (NotFoundException e) {
            logger.error("Car with id {} not found", kilometersDTO.getCarId());
            throw e;
        } catch (Exception e) {
            logger.error("Error saving kilometers: {}", e.getMessage());
            throw new ServiceException("Error saving kilometers");
        }
    }

    @Override
    @Transactional
    @CachePut(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_kilometers_', #id)")
    public void update(Long id, KilometersDTO kilometersDTO) {
        try {
            Kilometers kilometers = kilometersRepository.findById(id).orElseThrow(() -> new NotFoundException(KILOMETERS_NOT_FOUND));
            kilometers.setActualKm(kilometersDTO.getActualKm());
            kilometers.setUpdateKmDate(kilometersDTO.getUpdateKmDate());
            kilometersRepository.save(kilometers);

        } catch (NotFoundException e) {
            logger.error("Kilometers with id {} not found", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating kilometers: {}", e.getMessage());
            throw new ServiceException("Error updating kilometers");
        }
    }

    @Override
    @Transactional
    @CacheEvict(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_kilometers_', #id)")
    public void delete(Long id) {
        try {
            Kilometers kilometers = kilometersRepository.findById(id).orElseThrow(() -> new NotFoundException(KILOMETERS_NOT_FOUND));
            kilometersRepository.delete(kilometers);
        } catch (NotFoundException e) {
            logger.error("Kilometers with id {} not found", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting kilometers: {}", e.getMessage());
            throw new ServiceException("Error deleting kilometers");
        }
    }

    // Filters
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_kilometers_car_', #id)")
    public PageResponse<KilometersDTO> getByCarId(Long id, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Kilometers> kilometersPage = kilometersRepository.findByCar_CarId(id, pageable);

            if (kilometersPage.isEmpty()) {
                throw new NotFoundException(KILOMETERS_NOT_FOUND + " for car with id: " + id);
            }

            return mapToPageResponse(kilometersPage);
        } catch (NotFoundException e) {
            logger.error("Kilometers not found for car with id: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error getting kilometers by carId: {}", e.getMessage());
            throw new ServiceException("Error getting kilometers by carId");
        }
    }

    // Mappers
    // Map Entity to DTO
    private KilometersDTO mapToDTO(Kilometers kilometers) {
        return KilometersDTO.builder()
                .kilometersId(kilometers.getKilometersId())
                .updateKmDate(kilometers.getUpdateKmDate())
                .actualKm(kilometers.getActualKm())
                .carId(kilometers.getCar().getCarId())
                .build();
    }

    // Map DTO to Entity
    private Kilometers mapToEntity(KilometersDTO kilometersDTO) {
        return Kilometers.builder()
                .kilometersId(kilometersDTO.getKilometersId())
                .updateKmDate(kilometersDTO.getUpdateKmDate())
                .actualKm(kilometersDTO.getActualKm())
                .build();
    }

    private PageResponse<KilometersDTO> mapToPageResponse(Page<Kilometers> kilometersPage) {
        List<KilometersDTO> kilometersDTOList = kilometersPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                kilometersDTOList,
                kilometersPage.getNumber(),
                kilometersPage.getSize(),
                kilometersPage.getTotalElements(),
                kilometersPage.getTotalPages(),
                kilometersPage.isLast());
    }
}

