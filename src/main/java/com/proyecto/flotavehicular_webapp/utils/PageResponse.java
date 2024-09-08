package com.proyecto.flotavehicular_webapp.utils;

import java.util.List;

public record PageResponse<T>(List<T> items,
                              Integer pageNumber,
                              Integer pageSize,
                              Long totalElements,
                              int totalPages,
                              boolean isLast) {

    // Function to generate a new PageResponse
    public static <T> PageResponse<T> of(List<T> items, Integer pageNumber, Integer pageSize, Long totalElements, int totalPages, boolean isLast) {
        return new PageResponse<>(items, pageNumber, pageSize, totalElements, totalPages, isLast);
    }

}

