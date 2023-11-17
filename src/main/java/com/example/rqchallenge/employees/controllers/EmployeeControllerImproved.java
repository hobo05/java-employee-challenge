package com.example.rqchallenge.employees.controllers;

import com.example.rqchallenge.employees.models.Employee;
import com.example.rqchallenge.employees.models.NewEmployee;
import com.example.rqchallenge.employees.services.EmployeeService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

//@Component
public class EmployeeControllerImproved implements IEmployeeControllerImproved {

    private final EmployeeService employeeService;

    public EmployeeControllerImproved(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        return employeeService.getEmployeesByNameSearch(searchString);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeService.getEmployeeById(id);
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        return Optional.ofNullable(employeeService.getHighestSalaryEmployee())
                .map(Employee::salary)
                .orElse(null);
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        return employeeService.getTopTenHighestEarningEmployees().stream()
                .map(Employee::name)
                .toList();
    }

    @Override
    public Employee createEmployee(NewEmployee newEmployee) {
        return employeeService.createEmployee(newEmployee);
    }

    @Override
    public String deleteEmployeeById(Long id) {
        return employeeService.deleteEmployee(id).name();
    }
}
