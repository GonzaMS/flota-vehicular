package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.car.KilometersDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car.Car;
import com.proyecto.flotavehicular_webapp.models.Car.Kilometers;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
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
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Cacheable(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_kilometers_', #id)")
    public KilometersDTO getById(Long id) {
        Kilometers kilometers = kilometersRepository.findById(id).orElseThrow(() -> new NotFoundException(KILOMETERS_NOT_FOUND));
        return mapToDTO(kilometers);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 10)
    public Kilometers save(KilometersDTO kilometersDTO) {
        try {
            Car car = carRepository.findById(kilometersDTO.getCarId()).orElseThrow(() -> new NotFoundException("Car not found"));

            Kilometers kilometers = mapToEntity(kilometersDTO);
            kilometers.setCar(car);
            kilometers.setCreatedAt(new Date());

            return kilometersRepository.save(kilometers);
        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: kilometersDTO={}, carId={}", kilometersDTO, kilometersDTO.getCarId());
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error saving kilometers: {}, Parameters: kilometersDTO={}", e.getMessage(), kilometersDTO);
            throw new ServiceException("Error saving kilometers");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 10)
    @CachePut(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_kilometers_', #id)")
    public KilometersDTO update(Long id, KilometersDTO kilometersDTO) {
        try {
            Kilometers kilometers = kilometersRepository.findById(id).orElseThrow(() -> new NotFoundException(KILOMETERS_NOT_FOUND));

            kilometers.setActualKm(kilometersDTO.getActualKm());
            kilometers.setUpdatedAt(new Date());

            kilometersRepository.save(kilometers);

            return mapToDTO(kilometers);

        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: id={}, kilometersDTO={}", id, kilometersDTO);
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error updating Kilometers: {}, Parameters: id={}, carDTO={}", e.getMessage(), id, kilometersDTO);
            throw new ServiceException("Error updating kilometers");
        }
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = Exception.class, timeout = 10)
    @CacheEvict(cacheManager = "cacheManagerWithoutTtl", value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_kilometers_', #id)")
    public void delete(Long id) {
        try {
            Kilometers kilometers = kilometersRepository.findById(id).orElseThrow(() -> new NotFoundException(KILOMETERS_NOT_FOUND));
            kilometersRepository.delete(kilometers);
        } catch (NotFoundException e) {
            log.error("Rollback triggered - Parameters: id={}", id);
            throw e;
        } catch (Exception e) {
            log.error("Rollback triggered - Error deleting Kilometers: {}, Parameters: id={}", e.getMessage(), id);
            throw new ServiceException("Error deleting kilometers");
        }
    }

    // Filters
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, timeout = 5)
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
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, timeout = 5)
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

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, timeout = 5)
    @Cacheable(value = "sd", key = "T(com.proyecto.flotavehicular_webapp.utils.RedisUtils).CacheKeyGenerator('api_kilometers_', #carId, #startDate, #endDate)")
    public PageResponse<KilometersDTO> getByCarIdAndDate(Long carId, Date startDate, Date endDate, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Kilometers> kilometersPage = kilometersRepository.findByCarIdAndCreatedAtBetween(carId, startDate, endDate, pageable);

            if (kilometersPage.isEmpty()) {
                throw new NotFoundException(KILOMETERS_NOT_FOUND + " for car with id: " + carId + " between dates: " + startDate + " and " + endDate);
            }

            return mapToPageResponse(kilometersPage);
        } catch (NotFoundException e) {
            log.error("Kilometers not found for car with id: {} between dates: {} and {}", carId, startDate, endDate);
            throw e;
        } catch (Exception e) {
            log.error("Error getting kilometers by carId and date: {}", e.getMessage());
            throw new ServiceException("Error getting kilometers by carId and date");
        }
    }


    // Mappers
    // Map Entity to DTO
    private KilometersDTO mapToDTO(Kilometers kilometers) {
        return KilometersDTO.builder()
                .id(kilometers.getId())
                .createdAt(kilometers.getCreatedAt())
                .updatedAt(kilometers.getUpdatedAt())
                .actualKm(kilometers.getActualKm())
                .carId(kilometers.getCar().getId())
                .build();
    }

    // Map DTO to Entity
    private Kilometers mapToEntity(KilometersDTO kilometersDTO) {
        return Kilometers.builder()
                .id(kilometersDTO.getId())
                .createdAt(kilometersDTO.getCreatedAt())
                .updatedAt(kilometersDTO.getUpdatedAt())
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

