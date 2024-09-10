package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.KilometersDTO;
import com.proyecto.flotavehicular_webapp.models.Kilometers;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;

public interface IKilometersService {
    PageResponse<KilometersDTO> getAllKilometers(int pageNumber, int pageSize);

    KilometersDTO getKilometersById(Long id);

    Kilometers saveKilometers(KilometersDTO kilometersDTO);

    void updateKilometers(Long id, KilometersDTO kilometersDTO);

    void deleteKilometers(Long id);

    PageResponse<KilometersDTO> getKilometersByCarId(Long carId, int pageNumber, int pageSize);
}
