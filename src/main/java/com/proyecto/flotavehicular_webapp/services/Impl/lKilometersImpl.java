package com.proyecto.flotavehicular_webapp.services.Impl;

import com.proyecto.flotavehicular_webapp.dto.KilometersDTO;
import com.proyecto.flotavehicular_webapp.models.Kilometers;
import com.proyecto.flotavehicular_webapp.repositories.IKilometersRepository;
import com.proyecto.flotavehicular_webapp.services.IKilometersService;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class lKilometersImpl implements IKilometersService {

    private final IKilometersRepository kilometersRepository;

    public lKilometersImpl(IKilometersRepository kilometersRepository) {
        this.kilometersRepository = kilometersRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public PageResponse<KilometersDTO> getAllKilometers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Kilometers> kilometersPage = kilometersRepository.findAll(pageable);

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

    @Override
    public KilometersDTO getKilometersById(Long id) {
        return null;
    }

    @Override
    public Kilometers saveKilometers(KilometersDTO kilometersDTO) {
        return null;
    }

    @Override
    public void updateKilometers(Long id, KilometersDTO kilometersDTO) {

    }

    @Override
    public void deleteKilometers(Long id) {

    }

    private KilometersDTO mapToDTO(Kilometers kilometers) {
        return KilometersDTO.builder()
                .kilometersId(kilometers.getKilometersId())
                .actualKm(kilometers.getActualKm())
                .updateKmDate(kilometers.getUpdateKmDate().toString())
                .carId(kilometers.getCar().getCarId())
        .build();
    }
}
