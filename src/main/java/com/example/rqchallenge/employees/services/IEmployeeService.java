package com.example.rqchallenge.employees.services;

import com.example.rqchallenge.employees.models.Employee;
import com.example.rqchallenge.employees.models.NewEmployee;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IEmployeeService {
    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String searchString);

    Employee getEmployeeById(long id);

    Employee getHighestSalaryEmployee();

    List<Employee> getTopTenHighestEarningEmployees();

    Employee createEmployee(NewEmployee newEmployee);

    Employee deleteEmployee(long id);
}
