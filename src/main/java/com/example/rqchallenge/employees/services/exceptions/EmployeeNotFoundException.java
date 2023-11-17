package com.example.rqchallenge.employees.services.exceptions;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(long id) {
        super(String.format("Employee with id '%d' not found", id));
    }
}
