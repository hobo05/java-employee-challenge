package com.example.rqchallenge.employees.services.exceptions;

import com.example.rqchallenge.employees.models.NewEmployee;

public class EmployeeDataMissingException extends RuntimeException {

    private EmployeeDataMissingException(String message) {
        super(message);
    }

    public static EmployeeDataMissingException missingAfterFetch(Long id) {
        return new EmployeeDataMissingException(String.format("Employee with id '%d' found but employee data is missing", id));
    }

    public static EmployeeDataMissingException missingAfterCreate(NewEmployee newEmployee) {
        return new EmployeeDataMissingException(String.format("New employee [%s] created with 200 but employee data is missing", newEmployee));
    }
}
