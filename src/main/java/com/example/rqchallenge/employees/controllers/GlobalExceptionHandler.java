package com.example.rqchallenge.employees.controllers;

import com.example.rqchallenge.employees.services.exceptions.EmployeeDataMissingException;
import com.example.rqchallenge.employees.services.exceptions.EmployeeNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

import static com.example.rqchallenge.employees.controllers.ApiError.ErrorType.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> catchAllHandler(HttpServletRequest request, Exception ex) {
        log.error("Unhandled error for url: " + request.getRequestURL(), ex);

        return asResponse(new ApiError(SERVER_ERROR,
                "Unexpected error occurred. We will look into this ASAP",
                HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiError> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        return asResponse(new ApiError(MISSING_EMPLOYEE,
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(EmployeeDataMissingException.class)
    public ResponseEntity<ApiError> handleEmployeeDataMissingException(EmployeeDataMissingException ex) {
        log.error("Underlying Employee API has corrupt/missing data: ", ex);

        return asResponse(new ApiError(SERVER_ERROR,
                "Error retrieving employee",
                HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError> handleConstraintViolationException(Exception ex) {
        String errorMessage = ex.getMessage();
        if (ex instanceof MethodArgumentNotValidException argEx && argEx.hasFieldErrors()) {
            errorMessage = argEx.getFieldErrors().stream()
                    .map(it -> String.format("%s: %s", it.getField(), it.getDefaultMessage()))
                    .collect(Collectors.joining(", "));
        }

        return asResponse(new ApiError(INVALID_FIELDS,
                errorMessage,
                HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler({ConversionFailedException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiError> handleConversionFailedException() {
        return asResponse(new ApiError(INVALID_PARAMS,
                "Invalid path or query parameter",
                HttpStatus.BAD_REQUEST.value()));
    }

    private static ResponseEntity<ApiError> asResponse(ApiError apiError) {
        return ResponseEntity.status(HttpStatus.valueOf(apiError.status())).body(apiError);
    }
}
