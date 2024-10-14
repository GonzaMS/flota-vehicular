package com.proyecto.flotavehicular_webapp.utils;

import java.util.List;

public record PageResponse<T>(List<T> items,
                              Integer pageNumber,
                              Integer pageSize,
                              Long totalElements,
                              int totalPages,
                              boolean isLast) {

    // Factory method to create a new PageResponse instance
    public static <T> PageResponse<T> of(List<T> items, Integer pageNumber, Integer pageSize, Long totalElements, int totalPages, boolean isLast) {
        return new PageResponse<>(items, pageNumber, pageSize, totalElements, totalPages, isLast);
    }
}
