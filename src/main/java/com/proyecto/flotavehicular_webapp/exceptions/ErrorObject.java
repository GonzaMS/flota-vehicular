package com.proyecto.flotavehicular_webapp.exceptions;

import lombok.Data;

@Data
public class ErrorObject {
    private final String path;
    private final String error;
    private final String message;
    private final String timestamp;
    private final int status;
}