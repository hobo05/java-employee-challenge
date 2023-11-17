package com.example.rqchallenge.employees.controllers;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.example.rqchallenge.employees.utils.WiremockTestData.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@WireMockTest(httpPort = 9191)
class EmployeeControllerTest {

    MockMvc mvc;

    @BeforeEach
    public void setup(WebApplicationContext context) {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .defaultRequest(get("").contentType(MediaType.APPLICATION_JSON))
                .build();
    }

    @Test
    void can_retrieve_multiple_employees() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/employees").willReturn(WireMock.okJson(TWO_EMPLOYEES_RESPONSE)));

        // expect
        mvc.perform(get(""))
                .andExpect(content().json("""
                        [
                          {
                            "id": 1,
                            "name": "Tiger Nixon",
                            "salary": 320800,
                            "age": 61,
                            "profileImage": ""
                          },
                          {
                            "id": 2,
                            "name": "Garrett Winters",
                            "salary": 170750,
                            "age": 63,
                            "profileImage": ""
                          }
                        ]"""));
    }

    @Test
    void can_retrieve_a_single_employee() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/employee/1").willReturn(WireMock.okJson(SINGLE_EMPLOYEE_RESPONSE)));

        // expect
        mvc.perform(get("/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "id": 1,
                          "name": "Tiger Nixon",
                          "salary": 320800,
                          "age": 61,
                          "profileImage": ""
                        }"""));
    }

    @ParameterizedTest
    @MethodSource("provideMethodsWithIdParam")
    void returns_error_when_id_is_non_numerical(BiFunction<String, String, RequestBuilder> builder) throws Exception {
        // expect
        mvc.perform(builder.apply("/{id}", "not-an-id"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("INVALID_PARAMS")));
    }

    public static Stream<Arguments> provideMethodsWithIdParam() {
        return Stream.of(
                Arguments.of((BiFunction<String, String, RequestBuilder>) MockMvcRequestBuilders::get),
                Arguments.of((BiFunction<String, String, RequestBuilder>) MockMvcRequestBuilders::delete)
        );
    }

    @Test
    void return_404_when_getting_non_existent_employee() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/employee/404").willReturn(WireMock.jsonResponse(MISSING_EMPLOYEE_RESPONSE, 400)));

        // expect
        mvc.perform(get("/{id}", 404))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type", is("MISSING_EMPLOYEE")));
    }

    @Test
    void return_500_when_getting_employee_with_null_data() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/employee/1").willReturn(WireMock.okJson(NULL_EMPLOYEE_RESPONSE)));

        // expect
        mvc.perform(get("/{id}", 1))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.type", is("SERVER_ERROR")));
    }

    @Test
    void can_create_new_employee() throws Exception {
        // given
        String newEmployeeJson = """
                {
                  "name": "Son Goku",
                  "age": 88,
                  "salary": 50000
                }""";

        // and
        WireMock.stubFor(WireMock.post("/create").willReturn(WireMock.okJson(NEW_EMPLOYEE_CREATED_RESPONSE)));

        // expect
        mvc.perform(post("").content(newEmployeeJson))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "id": 1510,
                          "name": "Son Goku",
                          "age": 88,
                          "salary": 50000,
                          "profileImage": null
                        }
                        """));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "{}",
            "{\"name\":null,\"age\":88,\"salary\":50000}",
            "{\"name\":\"\",\"age\":88,\"salary\":50000}",
            "{\"name\":\"Son Goku\",\"age\":null,\"salary\":50000}",
            "{\"name\":\"Son Goku\",\"age\":88,\"salary\":null}"
    }, delimiter = '|')
    void new_employee_fields_are_validated(String newEmployeeJson) throws Exception {
        // and
        WireMock.stubFor(WireMock.post("/create").willReturn(WireMock.okJson(NEW_EMPLOYEE_CREATED_RESPONSE)));

        // expect
        mvc.perform(post("").content(newEmployeeJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("INVALID_FIELDS")));
    }

    @Test
    void can_delete_an_employee() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/employee/1").willReturn(WireMock.okJson(SINGLE_EMPLOYEE_RESPONSE)));

        // and
        WireMock.stubFor(WireMock.delete("/delete/1").willReturn(WireMock.okJson("""
                {
                  "status": "success",
                  "data": "1",
                  "message": "Successfully! Record has been deleted"
                }""")));

        // expect
        mvc.perform(delete("/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("Tiger Nixon"));
    }

    @Test
    void returns_404_when_deleting_non_existent_employee() throws Exception {
        // given
        WireMock.stubFor(WireMock.delete("/delete/404")
                .willReturn(WireMock.jsonResponse(MISSING_EMPLOYEE_RESPONSE, 400)));

        // expect
        mvc.perform(delete("/{id}", 404))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type", is("MISSING_EMPLOYEE")));
    }

    @Test
    void can_search_employee_by_name() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/employees").willReturn(WireMock.okJson(ALL_EMPLOYEES_RESPONSE)));

        // expect
        mvc.perform(get("/search/{searchString}", "fOo"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                [
                  {
                    "id": 1,
                    "name": "Foo Nixon",
                    "age": 61,
                    "salary": 120000,
                    profileImage: ""
                  },
                  {
                    "id": 2,
                    "name": "Garrett Foo",
                    "salary": 110000,
                    "age": 63,
                    "profileImage": ""
                  }
                ]"""));
    }

    @Test
    void can_return_highest_earning_employee() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/employees").willReturn(WireMock.okJson(ALL_EMPLOYEES_RESPONSE)));

        // expect
        mvc.perform(get("/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("120000"));
    }

    @Test
    void returns_nothing_if_no_employees() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/employees").willReturn(WireMock.okJson("{}")));

        // expect
        mvc.perform(get("/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void can_return_top_ten_highest_earners() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/employees").willReturn(WireMock.okJson(ALL_EMPLOYEES_RESPONSE)));

        // expect
        mvc.perform(get("/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          "Foo Nixon",
                          "Garrett Foo",
                          "sammy tWo $hoes 👞",
                          "Susie Solis",
                          "Zaara Wall",
                          "Edith Huber",
                          "Yasmine Cruz",
                          "Luc Christian",
                          "Nellie Maddox",
                          "Willie Washington"
                        ]"""));
    }

    @Test
    void returns_empty_list_of_top_earners_if_no_employees() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/employees").willReturn(WireMock.okJson("{}")));

        // expect
        mvc.perform(get("/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

}