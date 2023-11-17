package com.example.rqchallenge.employees.controllers;

import com.example.rqchallenge.employees.models.Employee;
import com.example.rqchallenge.employees.models.NewEmployee;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * - We don't need to return `ResponseEntity<T>` since we aren't returning other than 200s
 * - The ids can be directly mapped to `Long` and the Spring type conversion will handle the rest
 * - We can use `@Valid` and jakarta validation annotations to validate the new employee JSON
 */
@RestController
public interface IEmployeeControllerImproved {

    @GetMapping()
    List<Employee> getAllEmployees();

    @GetMapping("/search/{searchString}")
    List<Employee> getEmployeesByNameSearch(@PathVariable String searchString);

    @GetMapping("/{id}")
    Employee getEmployeeById(@PathVariable Long id);

    @GetMapping("/highestSalary")
    Integer getHighestSalaryOfEmployees();

    @GetMapping("/topTenHighestEarningEmployeeNames")
    List<String> getTopTenHighestEarningEmployeeNames();

    @PostMapping()
    Employee createEmployee(@Valid @RequestBody NewEmployee newEmployee);

    @DeleteMapping("/{id}")
    String deleteEmployeeById(@PathVariable Long id);

}
