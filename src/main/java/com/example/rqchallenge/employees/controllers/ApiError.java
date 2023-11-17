package com.example.rqchallenge.employees.controllers;

public record ApiError(ErrorType type, String message, int status) {

    public enum ErrorType {
        MISSING_EMPLOYEE,
        SERVER_ERROR,
        INVALID_FIELDS,
        INVALID_PARAMS,
    }
}
