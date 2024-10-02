package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.KilometersDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.models.Kilometers;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.repositories.IKilometersRepository;
import com.proyecto.flotavehicular_webapp.services.IKilometersService;
import com.proyecto.flotavehicular_webapp.services.Redis.RedisServiceImpl;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class IKilometersServiceImpl implements IKilometersService {

    private final IKilometersRepository kilometersRepository;
    private final ICarRepository carRepository;
    private final CacheManager cacheManager;
    private final RedisServiceImpl redisService;

    private static final String KILOMETERS_NOT_FOUND = "Kilometers not found";

    public IKilometersServiceImpl(IKilometersRepository kilometersRepository, ICarRepository carRepository, CacheManager cacheManager, RedisServiceImpl redisService) {
        this.kilometersRepository = kilometersRepository;
        this.carRepository = carRepository;
        this.cacheManager = cacheManager;
        this.redisService = redisService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<KilometersDTO> getAll(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Kilometers> kilometersPage = kilometersRepository.findAll(pageable);

            kilometersPage.forEach(kilometers -> {
                String key = RedisUtils.CacheKeyGenerator("sd::api_kilometers_", kilometers.getId());

                Object kilometersOnCache = redisService.get(key);

                if (kilometersOnCache == null) {
                    KilometersDTO kilometersDTO = mapToDTO(kilometers);
                    redisService.save(key, kilometersDTO);
                }
            });
            return mapToPageResponse(kilometersPage);
        } catch (Exception e) {
            log.error("Error getting all kilometers: {}", e.getMessage());
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
            log.error("Car with id {} not found", kilometersDTO.getCarId());
            throw e;
        } catch (Exception e) {
            log.error("Error saving kilometers: {}", e.getMessage());
            throw new ServiceException("Error saving kilometers");
        }
    }

    @Override
    @Transactional
    @CachePut(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_kilometers_', #id)")
    public KilometersDTO update(Long id, KilometersDTO kilometersDTO) {
        try {
            Kilometers kilometers = kilometersRepository.findById(id).orElseThrow(() -> new NotFoundException(KILOMETERS_NOT_FOUND));

            kilometers.setActualKm(kilometersDTO.getActualKm());

            kilometersRepository.save(kilometers);

            return mapToDTO(kilometers);

        } catch (NotFoundException e) {
            log.error("Kilometers with id {} not found", id);
            throw e;
        } catch (Exception e) {
            log.error("Error updating kilometers: {}", e.getMessage());
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
            log.error("Kilometers with id {} not found", id);
            throw e;
        } catch (Exception e) {
            log.error("Error deleting kilometers: {}", e.getMessage());
            throw new ServiceException("Error deleting kilometers");
        }
    }

    // Filters
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_kilometers_', #id)")
    public PageResponse<KilometersDTO> getByCarId(Long id, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Kilometers> kilometersPage = kilometersRepository.findByCarId(id, pageable);

            if (kilometersPage.isEmpty()) {
                throw new NotFoundException(KILOMETERS_NOT_FOUND + " for car with id: " + id);
            }

            return mapToPageResponse(kilometersPage);
        } catch (NotFoundException e) {
            log.error("Kilometers not found for car with id: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error getting kilometers by carId: {}", e.getMessage());
            throw new ServiceException("Error getting kilometers by carId");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_kilometers_', #startDate, #endDate)")
    public PageResponse<KilometersDTO> getByDate(Date startDate, Date endDate, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Kilometers> kilometersPage = kilometersRepository.findByCreatedAtBetween(startDate, endDate, pageable);

            if (kilometersPage.isEmpty()) {
                throw new NotFoundException(KILOMETERS_NOT_FOUND + " between dates: " + startDate + " and " + endDate);
            }

            return mapToPageResponse(kilometersPage);
        } catch (NotFoundException e) {
            log.error("Kilometers not found between dates: {} and {}", startDate, endDate);
            throw e;
        } catch (Exception e) {
            log.error("Error getting kilometers by date: {}", e.getMessage());
            throw new ServiceException("Error getting kilometers by date");
        }
    }


    // Mappers
    // Map Entity to DTO
    private KilometersDTO mapToDTO(Kilometers kilometers) {
        return KilometersDTO.builder()
                .id(kilometers.getId())
                .createdAt(kilometers.getCreatedAt())
                .actualKm(kilometers.getActualKm())
                .carId(kilometers.getCar().getId())
                .build();
    }

    // Map DTO to Entity
    private Kilometers mapToEntity(KilometersDTO kilometersDTO) {
        return Kilometers.builder()
                .id(kilometersDTO.getId())
                .createdAt(kilometersDTO.getCreatedAt())
                .actualKm(kilometersDTO.getActualKm())
                .build();
    }

    private PageResponse<KilometersDTO> mapToPageResponse(Page<Kilometers> kilometersPage) {
        List<KilometersDTO> kilometersDTOList = kilometersPage.stream().map(this::mapToDTO).toList();

        return PageResponse.of(kilometersDTOList, kilometersPage.getNumber(), kilometersPage.getSize(), kilometersPage.getTotalElements(), kilometersPage.getTotalPages(), kilometersPage.isLast());
    }
}

