package com.proyecto.flotavehicular_webapp.exceptions;

import com.proyecto.flotavehicular_webapp.exceptions.DTO.ErrorDTO;
import com.proyecto.flotavehicular_webapp.utils.ViewConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    // 404
    @ExceptionHandler(NotFoundException.class)
    public Object handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        String acceptHeader = request.getHeader(ViewConstants.REQUEST_HEADER);

        if (acceptHeader != null && acceptHeader.contains(ViewConstants.HEADER_CONTAINS_HTML)) {
            ModelAndView mav = new ModelAndView(ViewConstants.ERROR_404_PAGE);
            mav.addObject("message", ex.getMessage());
            return mav;
        }

        ErrorDTO errorObject = new ErrorDTO(
                request.getRequestURI(),
                "Not Found",
                ex.getMessage(),
                LocalDateTime.now().format(FORMATTER),
                HttpStatus.NOT_FOUND.value()
        );

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    // 404
    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleResourceNotFound(NoResourceFoundException ex, HttpServletRequest request) {
        String acceptHeader = request.getHeader(ViewConstants.REQUEST_HEADER);

        if (acceptHeader != null && acceptHeader.contains(ViewConstants.HEADER_CONTAINS_HTML)) {
            ModelAndView mav = new ModelAndView(ViewConstants.ERROR_404_PAGE);
            mav.addObject("message", ex.getMessage());
            return mav;
        }

        ErrorDTO errorObject = new ErrorDTO(
                request.getRequestURI(),
                "Not Found",
                ex.getMessage(),
                LocalDateTime.now().format(FORMATTER),
                HttpStatus.NOT_FOUND.value()
        );

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    // 400
    @ExceptionHandler(BadRequestException.class)
    public Object handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        String acceptHeader = request.getHeader(ViewConstants.REQUEST_HEADER);

        if (acceptHeader != null && acceptHeader.contains(ViewConstants.HEADER_CONTAINS_HTML)) {
            ModelAndView mav = new ModelAndView(ViewConstants.ERROR_400_PAGE);
            mav.addObject("message", ex.getMessage());
            return mav;
        }

        ErrorDTO errorObject = new ErrorDTO(
                request.getRequestURI(),
                "Bad Request",
                ex.getMessage(),
                LocalDateTime.now().format(FORMATTER),
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    // 404
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String acceptHeader = request.getHeader(ViewConstants.REQUEST_HEADER);

        if (acceptHeader != null && acceptHeader.contains(ViewConstants.HEADER_CONTAINS_HTML)) {
            ModelAndView mav = new ModelAndView(ViewConstants.ERROR_400_PAGE);
            mav.addObject("message", ex.getMessage());
            return mav;
        }

        ErrorDTO errorObject = new ErrorDTO(
                request.getRequestURI(),
                "Not Found",
                ex.getMessage(),
                LocalDateTime.now().format(FORMATTER),
                HttpStatus.NOT_FOUND.value()
        );

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    // 400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String acceptHeader = request.getHeader(ViewConstants.REQUEST_HEADER);

        if (acceptHeader != null && acceptHeader.contains(ViewConstants.HEADER_CONTAINS_HTML)) {
            ModelAndView mav = new ModelAndView(ViewConstants.ERROR_400_PAGE);
            mav.addObject("message", "Invalid argument type: " + ex.getName());
            return mav;
        }

        ErrorDTO errorObject = new ErrorDTO(
                request.getRequestURI(),
                "Bad Request",
                "Invalid argument type: " + ex.getName(),
                LocalDateTime.now().format(FORMATTER),
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    // 500
    @ExceptionHandler(InternalError.class)
    public Object handleInternalErrorException(InternalError ex, HttpServletRequest request) {
        String acceptHeader = request.getHeader(ViewConstants.REQUEST_HEADER);

        if (acceptHeader != null && acceptHeader.contains(ViewConstants.HEADER_CONTAINS_HTML)) {
            ModelAndView mav = new ModelAndView(ViewConstants.ERROR_500_PAGE);
            mav.addObject("message", ex.getMessage());
            return mav;
        }

        ErrorDTO errorObject = new ErrorDTO(
                request.getRequestURI(),
                "Internal Server Error",
                ex.getMessage(),
                LocalDateTime.now().format(FORMATTER),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return new ResponseEntity<>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 500
    @ExceptionHandler(HttpClientErrorException.class)
    public Object handleHttpClientErrorException(HttpClientErrorException ex, HttpServletRequest request) {
        String acceptHeader = request.getHeader(ViewConstants.REQUEST_HEADER);

        if (acceptHeader != null && acceptHeader.contains(ViewConstants.HEADER_CONTAINS_HTML)) {
            ModelAndView mav = new ModelAndView(ViewConstants.ERROR_500_PAGE);
            mav.addObject("message", ex.getMessage());
            return mav;
        }

        ErrorDTO errorObject = new ErrorDTO(
                request.getRequestURI(),
                "Internal Server Error",
                ex.getMessage(),
                LocalDateTime.now().format(FORMATTER),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return new ResponseEntity<>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}