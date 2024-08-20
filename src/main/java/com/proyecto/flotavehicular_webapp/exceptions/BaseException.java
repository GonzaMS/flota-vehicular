package com.proyecto.flotavehicular_webapp.exceptions;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseException extends RuntimeException {
    protected final HttpStatus status;

    public BaseException(HttpStatus status,String message) {
        super(message);
        this.status = status;
    }

    public BaseException(String message){
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
