package com.proyecto.flotavehicular_webapp.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtil {

    // Crea un Pageable con sort
    public static Pageable createPageable(Integer pageNumber,
                                          Integer pageSize,
                                          String sortBy,
                                          String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    // Crea un Pageable sin sort
    public static Pageable createPageable(Integer pageNumber,
                                          Integer pageSize) {
        return PageRequest.of(pageNumber, pageSize);
    }

    // Validacion
    public static Pageable validateAndCreatePageable(Integer pageNumber,
                                                     Integer pageSize,
                                                     String sortBy,
                                                     String direction) {
        if (pageNumber == null || pageNumber < 0) pageNumber = 0;
        if (pageSize == null || pageSize <= 0) pageSize = 10;  // Default page size
        if (sortBy == null || sortBy.isEmpty()) sortBy = "fabricationYear"; // Default sort field corrected
        if (direction == null || direction.isEmpty()) direction = "ASC"; // Default sort direction

        return createPageable(pageNumber, pageSize, sortBy, direction);
    }

}
