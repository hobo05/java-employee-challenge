package com.example.rqchallenge.employees.controllers;

import com.example.rqchallenge.employees.services.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
public class UnhandledExceptionTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    EmployeeService employeeService;

    @Test
    public void does_not_leak_internal_exception_info() throws Exception {
        // given
        given(employeeService.getAllEmployees())
                .willThrow(new TestInternalUnhandledException("some internal error message that exposes implementation details"));

        // expect
        mvc.perform(MockMvcRequestBuilders.get(""))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("""
                        {
                          "type": "SERVER_ERROR",
                          "message": "Unexpected error occurred. We will look into this ASAP",
                          "status": 500
                        }
                        """));
    }

    public static class TestInternalUnhandledException extends RuntimeException {
        public TestInternalUnhandledException(String message) {
            super(message);
        }
    }
}
