package com.example.rqchallenge.employees.utils;

import com.example.dummy.model.EmployeeResponseModel;
import com.example.dummy.model.EmployeeUpdateModel;
import com.example.dummy.model.EmployeeUpdateResponseData;
import com.example.rqchallenge.employees.models.Employee;
import com.example.rqchallenge.employees.models.NewEmployee;

import java.util.Map;

public class EmployeeMapper {
    public static Employee mapToEmployee(EmployeeResponseModel responseModel) {
        return new Employee(responseModel.getId(),
                responseModel.getEmployeeName(),
                responseModel.getEmployeeAge(),
                responseModel.getEmployeeSalary(),
                responseModel.getProfileImage());
    }

    public static Employee mapToEmployee(EmployeeUpdateResponseData updateResponseData) {
        return new Employee(updateResponseData.getId(),
                updateResponseData.getName(),
                updateResponseData.getAge(),
                updateResponseData.getSalary(),
                null);
    }

    public static EmployeeUpdateModel mapToEmployeeUpdateModel(NewEmployee newEmployee) {
        return new EmployeeUpdateModel()
                .name(newEmployee.name())
                .age(newEmployee.age())
                .salary(newEmployee.salary());
    }

    public static NewEmployee mapToNewEmployee(Map<String, Object> newEmployeeMap) {
        return new NewEmployee(safeCastToString(newEmployeeMap.get("name")),
                safeCastToInteger(newEmployeeMap.get("age")),
                safeCastToInteger(newEmployeeMap.get("salary")));
    }

    private static String safeCastToString(Object value) {
        if (value instanceof String) {
            return (String) value;
        } else {
            return null;
        }
    }

    private static Integer safeCastToInteger(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return null;
        }
    }
}
