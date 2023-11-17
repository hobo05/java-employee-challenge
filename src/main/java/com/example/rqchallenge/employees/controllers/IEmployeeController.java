package com.example.rqchallenge.employees.controllers;

import com.example.rqchallenge.employees.models.Employee;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Tag(name = "employee", description = "the employee API")
@RestController
public interface IEmployeeController {

    @Operation(summary = "Get all employees", description = "Retrieves all employees as a list", tags = {"employee"})
    @ApiResponses(value = {@ApiResponse(description = "successful operation",
            content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Employee.class)))})})
    @GetMapping()
    ResponseEntity<List<Employee>> getAllEmployees() throws IOException;

    @Operation(summary = "Search employees", description = "Return all employees whose name contains or matches the string input provided", tags = {"employee"})
    @ApiResponses(value = {@ApiResponse(description = "successful operation",
            content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Employee.class)))})})
    @GetMapping("/search/{searchString}")
    ResponseEntity<List<Employee>> getEmployeesByNameSearch(@Parameter(description = "Search string") @PathVariable String searchString);

    // TODO ideally the id here should be of type long so spring will automatically raise a 400 if a non-numerical value is passed in
    @Operation(summary = "Get employee by id", tags = {"employee"})
    @ApiResponses(value = {@ApiResponse(description = "successful operation",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))})})
    @GetMapping("/{id}")
    ResponseEntity<Employee> getEmployeeById(@Parameter(description = "employee id") @PathVariable String id);

    @Operation(summary = "Get highest salary of all employees", tags = {"employee"})
    @ApiResponses(value = {@ApiResponse(description = "successful operation", content = {@Content(mediaType = "text/plain")})})
    @GetMapping("/highestSalary")
    ResponseEntity<Integer> getHighestSalaryOfEmployees();

    @Operation(summary = "Get top ten highest earning employee names", tags = {"employee"})
    @ApiResponses(value = {@ApiResponse(description = "successful operation",
            content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(type = "string", example = "John Smith")))})})
    @GetMapping("/topTenHighestEarningEmployeeNames")
    ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames();

    // TODO it would be better to use a POJO with validation annotations and @Valid
    @Operation(summary = "Create an employee", tags = {"employee"})
    @ApiResponses(value = {@ApiResponse(description = "successful operation",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))})})
    @PostMapping()
    ResponseEntity<Employee> createEmployee(@Parameter(description = "new employee details",
            schema = @Schema(type = "object", example = "{ \"name\": \"John Smith\", \"age\": 24, \"salary\": 50000 }"))
                                            @RequestBody Map<String, Object> employeeInput);

    // TODO this can't really be tested because the underlying API does not return the name of the employee
    @Operation(summary = "Delete employee by id", tags = {"employee"})
    @ApiResponses(value = {@ApiResponse(description = "successful operation",
            content = {@Content(mediaType = "application/json", schema = @Schema(type = "string", example = "John Smith"))})})
    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteEmployeeById(@Parameter(description = "employee id") @PathVariable String id);

}
