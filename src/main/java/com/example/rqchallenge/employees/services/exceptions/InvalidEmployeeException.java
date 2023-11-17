package com.example.rqchallenge.employees.services.exceptions;

public class InvalidEmployeeException extends RuntimeException {
    public InvalidEmployeeException(String message) {
        super(message);
    }
}
