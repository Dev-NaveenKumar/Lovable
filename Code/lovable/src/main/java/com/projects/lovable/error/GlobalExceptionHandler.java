package com.projects.lovable.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleBadRequestException(ResourceNotFoundException ex){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND,
                ex.getResourceName()+" with ID "+ex.getResourceId()+" not found.");
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleInputValidationError(MethodArgumentNotValidException ex){
        var fieldErrorList = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new ApiFieldError(err.getField(), err.getDefaultMessage()))
                .toList();


        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,
                "Input validation failed",fieldErrorList);
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }
}
