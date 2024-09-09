package com.proyecto.flotavehicular_webapp.exceptions.DTO;

import lombok.Data;

@Data
public class ErrorDTO {
    private final String path;
    private final String error;
    private final String message;
    private final String timestamp;
    private final int status;
}