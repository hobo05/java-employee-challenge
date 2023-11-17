package com.example.rqchallenge.employees.utils;

public class WiremockTestData {

    public static final String TWO_EMPLOYEES_RESPONSE = """
            {
              "status": "success",
              "data": [
                {
                  "id": 1,
                  "employee_name": "Tiger Nixon",
                  "employee_salary": 320800,
                  "employee_age": 61,
                  "profile_image": ""
                },
                {
                  "id": 2,
                  "employee_name": "Garrett Winters",
                  "employee_salary": 170750,
                  "employee_age": 63,
                  "profile_image": ""
                }
              ],
              "message": "Successfully! All records has been fetched."
            }""";

    public static final String SINGLE_EMPLOYEE_RESPONSE = """
            {
              "status": "success",
              "data": {
                "id": 1,
                "employee_name": "Tiger Nixon",
                "employee_salary": 320800,
                "employee_age": 61,
                "profile_image": ""
              },
              "message": "Successfully! Record has been fetched."
            }""";

    public static final String NEW_EMPLOYEE_CREATED_RESPONSE = """
            {
              "status": "success",
              "data": {
                "name": "Son Goku",
                "salary": 50000,
                "age": 88,
                "id": 1510
              },
              "message": "Successfully! Record has been added."
            }""";

    public static final String MISSING_EMPLOYEE_RESPONSE = """
            {
              "status": "error",
              "message": "Not found record",
              "code": 400,
              "errors": "id is empty"
            }""";

    public static final String NULL_EMPLOYEE_RESPONSE = """
            {
              "status": "success",
              "data": null,
              "message": "Successfully! Record has been fetched."
            }""";

    public static final String ALL_EMPLOYEES_RESPONSE = """
            {
              "status": "success",
              "data": [
                {
                  "id": 1,
                  "employee_name": "Foo Nixon",
                  "employee_salary": 120000,
                  "employee_age": 61,
                  "profile_image": ""
                },
                {
                  "id": 2,
                  "employee_name": "Garrett Foo",
                  "employee_salary": 110000,
                  "employee_age": 63,
                  "profile_image": ""
                },
                {
                  "id": 3,
                  "employee_name": "sammy tWo $hoes 👞",
                  "employee_salary": 90000,
                  "employee_age": 33,
                  "profile_image": ""
                },
                                {
                  "id": 4,
                  "employee_name": "Susie Solis",
                  "employee_salary": 90000,
                  "employee_age": 33,
                  "profile_image": ""
                },
                                {
                  "id": 5,
                  "employee_name": "Zaara Wall",
                  "employee_salary": 80000,
                  "employee_age": 33,
                  "profile_image": ""
                },
                                {
                  "id": 6,
                  "employee_name": "Edith Huber",
                  "employee_salary": 70000,
                  "employee_age": 33,
                  "profile_image": ""
                },
                                {
                  "id": 7,
                  "employee_name": "Yasmine Cruz",
                  "employee_salary": 60000,
                  "employee_age": 33,
                  "profile_image": ""
                },
                                {
                  "id": 8,
                  "employee_name": "Luc Christian",
                  "employee_salary": 50000,
                  "employee_age": 33,
                  "profile_image": ""
                },
                                {
                  "id": 9,
                  "employee_name": "Nellie Maddox",
                  "employee_salary": 40000,
                  "employee_age": 33,
                  "profile_image": ""
                },
                                {
                  "id": 10,
                  "employee_name": "Willie Washington",
                  "employee_salary": 30000,
                  "employee_age": 33,
                  "profile_image": ""
                },
                                {
                  "id": 11,
                  "employee_name": "Sonny Giles",
                  "employee_salary": 20000,
                  "employee_age": 33,
                  "profile_image": ""
                },
                                {
                  "id": 12,
                  "employee_name": "Melisa Ferrell",
                  "employee_salary": 10000,
                  "employee_age": 33,
                  "profile_image": ""
                }
              ],
              "message": "Successfully! All records has been fetched."
            }""";
}
