package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.KilometersDTO;
import com.proyecto.flotavehicular_webapp.models.Kilometers;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;

import java.util.Date;

public interface IKilometersService {
    PageResponse<KilometersDTO> getAll(int pageNumber, int pageSize);

    KilometersDTO getById(Long id);

    Kilometers save(KilometersDTO kilometersDTO);

    KilometersDTO update(Long id, KilometersDTO kilometersDTO);

    void delete(Long id);

    // Filter
    PageResponse<KilometersDTO> getByCarId(Long carId, int pageNumber, int pageSize);

    PageResponse<KilometersDTO> getByDate(Date startDate, Date endDate, int pageNumber, int pageSize);
}
