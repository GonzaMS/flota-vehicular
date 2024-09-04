package com.proyecto.flotavehicular_webapp.dto.Pageables;

import com.proyecto.flotavehicular_webapp.dto.CarDTO;

import java.util.List;

public record CarPageResponse (List<CarDTO> carsList,
                               Integer pageNumber,
                               Integer pageSize,
                               int totalElements,
                               int totalPages,
                               boolean isLast){

}
