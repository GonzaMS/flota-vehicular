package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.CarIncidentsDTO;
import com.proyecto.flotavehicular_webapp.exceptions.NotFoundException;
import com.proyecto.flotavehicular_webapp.models.Car;
import com.proyecto.flotavehicular_webapp.models.CarIncidents;
import com.proyecto.flotavehicular_webapp.repositories.ICarIncidentsRepository;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.services.ICarIncidentsService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
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


    public ICarIncidentsServiceImpl(ICarIncidentsRepository incidentRepository, ICarRepository carRepository) {
        this.carIncidentsRepository = incidentRepository;
        this.carRepository = carRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CarIncidentsDTO> getAllIncidents(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber,pageSize);

        Page<CarIncidents> incidentPage = carIncidentsRepository.findAll(pageable);

        List<CarIncidentsDTO> carIncidentsDTOList = incidentPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                carIncidentsDTOList,
                incidentPage.getNumber(),
                incidentPage.getSize(),
                incidentPage.getTotalElements(),
                incidentPage.getTotalPages(),
                incidentPage.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public CarIncidentsDTO getIncidentById(Long id) {
        CarIncidents carIncidents = carIncidentsRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        return mapToDTO(carIncidents);
    }

    @Override
    @Transactional
    public CarIncidents saveIncident(CarIncidentsDTO carIncidentsDTO) {

        Car car = carRepository.findById(carIncidentsDTO.getCarId()).orElseThrow(() -> new NotFoundException("Car not found"));

        CarIncidents carIncidents = mapToEntity(carIncidentsDTO);
        carIncidents.setCar(car);

        return carIncidentsRepository.save(carIncidents);
    }

    @Override
    @Transactional
    public void updateIncident(Long id, CarIncidentsDTO carIncidentsDTO) {
        CarIncidents carIncidents = carIncidentsRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));

        carIncidents.setIncidentDate(carIncidentsDTO.getIncidentDate());
        carIncidents.setIncidentDescription(carIncidentsDTO.getIncidentDescription());
        carIncidents.setIncidentType(carIncidentsDTO.getIncidentType());

        carIncidentsRepository.save(carIncidents);
    }

    @Override
    @Transactional
    public void deleteIncident(Long id) {
        CarIncidents carIncidents = carIncidentsRepository.findById(id).orElseThrow(() -> new NotFoundException(NOTFOUND));
        carIncidentsRepository.delete(carIncidents);
    }

    // Filters
    @Override
    public PageResponse<CarIncidentsDTO> getIncidentsByCarId(Long id, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber,pageSize);

        Page<CarIncidents> carIncidentsPage = carIncidentsRepository.findByCar_CarId(id, pageable);

        if(carIncidentsPage.isEmpty()){
            throw new NotFoundException(NOTFOUND + " for car with id: " + id);
        }

        List<CarIncidentsDTO> carIncidentsDTOList = carIncidentsPage.stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.of(
                carIncidentsDTOList,
                carIncidentsPage.getNumber(),
                carIncidentsPage.getSize(),
                carIncidentsPage.getTotalElements(),
                carIncidentsPage.getTotalPages(),
                carIncidentsPage.isLast()
        );
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
}
