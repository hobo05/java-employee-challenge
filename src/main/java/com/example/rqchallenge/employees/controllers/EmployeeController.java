package com.example.rqchallenge.employees.controllers;

import com.example.rqchallenge.employees.models.Employee;
import com.example.rqchallenge.employees.models.NewEmployee;
import com.example.rqchallenge.employees.services.EmployeeService;
import com.example.rqchallenge.employees.utils.EmployeeMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class EmployeeController implements IEmployeeController {

    private final EmployeeService employeeService;

    private final Validator validator;

    private final ConversionService conversionService;

    public EmployeeController(EmployeeService employeeService, Validator validator, ConversionService conversionService) {
        this.employeeService = employeeService;
        this.validator = validator;
        this.conversionService = conversionService;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        return ResponseEntity.ok(employeeService.getEmployeesByNameSearch(searchString));
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        long convertedId = Optional.ofNullable(conversionService.convert(id, Long.class)).orElseThrow();
        return ResponseEntity.ok(employeeService.getEmployeeById(convertedId));
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Integer highestSalary = Optional.ofNullable(employeeService.getHighestSalaryEmployee())
                .map(Employee::salary)
                .orElse(null);
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.ok(employeeService.getTopTenHighestEarningEmployees().stream()
                .map(Employee::name)
                .toList());
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
        NewEmployee newEmployee = EmployeeMapper.mapToNewEmployee(employeeInput);
        var constraintViolations = validator.validate(newEmployee);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }

        return ResponseEntity.ok(employeeService.createEmployee(newEmployee));
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        long convertedId = Optional.ofNullable(conversionService.convert(id, Long.class)).orElseThrow();
        Employee employee = employeeService.deleteEmployee(convertedId);
        return ResponseEntity.ok(employee.name());
    }
}
