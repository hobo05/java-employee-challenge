package com.example.rqchallenge.employees.utils;

import com.example.dummy.model.EmployeeResponseModel;
import com.example.dummy.model.EmployeeUpdateModel;
import com.example.dummy.model.EmployeeUpdateResponseData;
import com.example.rqchallenge.employees.models.Employee;
import com.example.rqchallenge.employees.models.NewEmployee;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeMapperTest {

    @Test
    void can_map_all_null_employee_response_model_to_employee() {
        // given
        var model = new EmployeeResponseModel();

        // when
        var employee = EmployeeMapper.mapToEmployee(model);

        // then
        assertThat(employee).isEqualTo(new Employee(null, null, null, null, null));
    }

    @Test
    void can_map_populated_employee_response_model_to_employee() {
        // given
        var model = new EmployeeResponseModel()
                .id(123L)
                .employeeName("Joe Schmoe")
                .employeeAge(22)
                .employeeSalary(45_000)
                .profileImage("test profile pic");


        // when
        var employee = EmployeeMapper.mapToEmployee(model);

        // then
        assertThat(employee).isEqualTo(new Employee(
                123L,
                "Joe Schmoe",
                22,
                45_000,
                "test profile pic"));
    }

    @Test
    void can_map_all_null_employee_update_model_to_employee() {
        // given
        var model = new EmployeeUpdateResponseData();

        // when
        var employee = EmployeeMapper.mapToEmployee(model);

        // then
        assertThat(employee).isEqualTo(new Employee(null, null, null, null, null));
    }

    @Test
    void can_map_populated_employee_update_model_to_employee() {
        // given
        var responseData = new EmployeeUpdateResponseData()
                .id(123L)
                .name("Joe Schmoe")
                .age(22)
                .salary(45_000);

        // when
        var employee = EmployeeMapper.mapToEmployee(responseData);

        // then
        assertThat(employee).isEqualTo(new Employee(
                123L,
                "Joe Schmoe",
                22,
                45_000,
                null));
    }

    @Test
    void can_map_all_null_new_employee_to_employee_update_model() {
        // given
        var newEmployee = new NewEmployee(null, null, null);

        // when
        var employeeUpdateModel = EmployeeMapper.mapToEmployeeUpdateModel(newEmployee);

        // then
        assertThat(employeeUpdateModel).isEqualTo(new EmployeeUpdateModel());
    }

    @Test
    void can_map_populated_new_employee_to_employee_update_model() {
        // given
        var newEmployee = new NewEmployee("Joe Schmoe", 22, 45_000);

        // when
        var employeeUpdateModel = EmployeeMapper.mapToEmployeeUpdateModel(newEmployee);

        // then
        assertThat(employeeUpdateModel).isEqualTo(new EmployeeUpdateModel()
                .name("Joe Schmoe")
                .age(22)
                .salary(45_000));
    }

    @Test
    void can_map_empty_new_employee_map_to_new_employee() {
        // given
        Map<String, Object> newEmployeeMap = Collections.emptyMap();

        // when
        var newEmployee = EmployeeMapper.mapToNewEmployee(newEmployeeMap);

        // then
        assertThat(newEmployee).isEqualTo(new NewEmployee(null, null, null));
    }

    @Test
    void can_map_populated_new_employee_map_to_new_employee() {
        // given
        Map<String, Object> newEmployeeMap = Map.of(
                "name", "foo bar",
                "age", 123,
                "salary", 321
        );

        // when
        var newEmployee = EmployeeMapper.mapToNewEmployee(newEmployeeMap);

        // then
        assertThat(newEmployee).isEqualTo(new NewEmployee("foo bar", 123, 321));
    }

    @Test
    void value_with_unexpected_types_and_keys_are_ignored_gracefully() {
        // given
        Map<String, Object> newEmployeeMap = Map.of(
                "name", 666,
                "age", "123",
                "employee_age", 23,
                "salary", 321
        );

        // when
        var newEmployee = EmployeeMapper.mapToNewEmployee(newEmployeeMap);

        // then
        assertThat(newEmployee).isEqualTo(new NewEmployee(null, null, 321));
    }
}