package com.proyecto.flotavehicular_webapp.dto.Pageables;

import com.proyecto.flotavehicular_webapp.dto.DriverDTO;

import java.util.List;

public record DriverPageResponse(List<DriverDTO> driverList,
                                Integer pageNumber,
                                Integer pageSize,
                                int totalElements,
                                int totalPages,
                                boolean isLast) {
}
