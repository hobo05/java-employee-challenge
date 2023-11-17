package com.example.rqchallenge.employees.services;

import com.example.dummy.api.EmployeeApi;
import com.example.dummy.model.EmployeeUpdateResponse;
import com.example.rqchallenge.employees.models.Employee;
import com.example.rqchallenge.employees.models.NewEmployee;
import com.example.rqchallenge.employees.services.exceptions.EmployeeDataMissingException;
import com.example.rqchallenge.employees.services.exceptions.EmployeeNotFoundException;
import com.example.rqchallenge.employees.services.exceptions.InvalidEmployeeException;
import com.example.rqchallenge.employees.utils.EmployeeMapper;
import feign.FeignException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Component
public class EmployeeService implements IEmployeeService {

    private final EmployeeApi employeeApi;

    public EmployeeService(EmployeeApi employeeApi) {
        this.employeeApi = employeeApi;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return getAllEmployeesStream()
                .toList();
    }

    // TODO we are not handling search terms that contain accents. this could be achieved by doing ASCII folding
    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        if (searchString == null) {
            return Collections.emptyList();
        }

        return getAllEmployeesStream().filter(it -> it.name().toLowerCase().contains(searchString.toLowerCase()))
                .toList();
    }

    @Override
    public Employee getEmployeeById(long id) {
        try {
            return Optional.ofNullable(employeeApi.getEmployeeById(id).getData())
                    .map(EmployeeMapper::mapToEmployee)
                    .orElseThrow(() -> EmployeeDataMissingException.missingAfterFetch(id));
        } catch (FeignException.NotFound e) {
            throw new EmployeeNotFoundException(id);
        }
    }

    // TODO what about a tie-breaker situation?
    @Override
    public Employee getHighestSalaryEmployee() {
        return getAllEmployeesStream()
                .max(Comparator.comparingInt(Employee::salary))
                .orElse(null);
    }

    @Override
    public List<Employee> getTopTenHighestEarningEmployees() {
        return getAllEmployeesStream()
                .sorted(Comparator.comparingInt(Employee::salary).reversed())
                .limit(10)
                .toList();
    }

    @Override
    public Employee createEmployee(NewEmployee newEmployee) {
        if (newEmployee == null) {
            throw new InvalidEmployeeException("NewEmployee cannot be null");
        }

        EmployeeUpdateResponse updateResponse = employeeApi.addEmployee(EmployeeMapper.mapToEmployeeUpdateModel(newEmployee));
        return Optional.ofNullable(updateResponse.getData())
                .map(EmployeeMapper::mapToEmployee)
                .orElseThrow(() -> EmployeeDataMissingException.missingAfterCreate(newEmployee));
    }

    @Override
    public Employee deleteEmployee(long id) {
        try {
            Employee employee = getEmployeeById(id);
            employeeApi.deleteEmployeeById(id);
            return employee;
        } catch (FeignException.NotFound e) {
            throw new EmployeeNotFoundException(id);
        }
    }

    @NotNull
    private Stream<Employee> getAllEmployeesStream() {
        return Optional.ofNullable(employeeApi.getAllEmployees().getData()).orElse(Collections.emptyList())
                .stream()
                .map(EmployeeMapper::mapToEmployee);
    }
}
