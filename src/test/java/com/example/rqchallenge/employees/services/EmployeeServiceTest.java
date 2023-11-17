package com.example.rqchallenge.employees.services;

import com.example.rqchallenge.employees.models.Employee;
import com.example.rqchallenge.employees.models.NewEmployee;
import com.example.rqchallenge.employees.services.exceptions.EmployeeDataMissingException;
import com.example.rqchallenge.employees.services.exceptions.EmployeeNotFoundException;
import com.example.rqchallenge.employees.services.exceptions.InvalidEmployeeException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import feign.RetryableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.stream.Stream;

import static com.example.rqchallenge.employees.utils.WiremockTestData.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Arrays.array;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
@WireMockTest(httpPort = 9191)
class EmployeeServiceTest {

    @Autowired
    EmployeeService employeeService;

    @ParameterizedTest
    @CsvSource(value = {
            "{\"status\":\"success\",\"data\":[],\"message\":\"Successfully! All records has been fetched.\"}",
            "{\"status\":\"success\",\"data\":null,\"message\":\"Successfully! All records has been fetched.\"}",
            "{}"
    }, delimiter = '|')
    void return_empty_list_when_no_employees_exist() {
        // given
        stubFor(get("/employees").willReturn(okJson("""
                {
                  "status": "success",
                  "data": [],
                  "message": "Successfully! All records has been fetched."
                }""")));

        // when
        List<Employee> response = employeeService.getAllEmployees();

        // then
        assertThat(response).isEmpty();
    }

    @Test
    void can_retrieve_multiple_employees() {
        // given
        stubFor(get("/employees").willReturn(okJson(TWO_EMPLOYEES_RESPONSE)));

        // when
        List<Employee> response = employeeService.getAllEmployees();

        // then
        assertThat(response).containsExactly(
                new Employee(1L, "Tiger Nixon", 61, 320_800, ""),
                new Employee(2L, "Garrett Winters", 63, 170_750, "")
        );
    }

    @Test
    void can_retrieve_a_single_employee() {
        // given
        stubFor(get("/employee/1").willReturn(okJson(SINGLE_EMPLOYEE_RESPONSE)));

        // when
        Employee response = employeeService.getEmployeeById(1L);

        // then
        assertThat(response).isEqualTo(new Employee(1L, "Tiger Nixon", 61, 320_800, ""));
    }

    @Test
    void return_error_when_getting_non_existent_employee() {
        // given
        stubFor(get("/employee/404").willReturn(jsonResponse(MISSING_EMPLOYEE_RESPONSE, 400)));

        // expect
        assertThatThrownBy(() -> employeeService.getEmployeeById(404L))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void return_error_when_getting_employee_with_null_data() {
        // given
        stubFor(get("/employee/1").willReturn(okJson(NULL_EMPLOYEE_RESPONSE)));

        // expect
        assertThatThrownBy(() -> employeeService.getEmployeeById(1L))
                .isInstanceOf(EmployeeDataMissingException.class);
    }

    @Test
    void can_create_new_employee() {
        // given
        NewEmployee newEmployee = new NewEmployee("Son Goku", 88, 50_000);

        // and
        stubFor(WireMock.post("/create").willReturn(okJson(NEW_EMPLOYEE_CREATED_RESPONSE)));

        // when
        Employee response = employeeService.createEmployee(newEmployee);

        // then
        assertThat(response).isEqualTo(new Employee(1510L, "Son Goku", 88, 50_000, null));
    }

    @Test
    void return_error_when_creating_invalid_employee() {
        // expect
        assertThatThrownBy(() -> employeeService.createEmployee(null))
                .isInstanceOf(InvalidEmployeeException.class);
    }

    @Test
    void can_delete_an_employee() {
        // given
        stubFor(get("/employee/1").willReturn(okJson(SINGLE_EMPLOYEE_RESPONSE)));

        // and
        stubFor(delete("/delete/1").willReturn(okJson("""
                {
                  "status": "success",
                  "data": "1",
                  "message": "Successfully! Record has been deleted"
                }""")));

        // when
        Employee employee = employeeService.deleteEmployee(1L);

        // then
        assertThat(employee.name()).isEqualTo("Tiger Nixon");
    }

    @Test
    void returns_error_when_deleting_non_existent_employee() {
        // given
        stubFor(delete("/delete/404").willReturn(jsonResponse(MISSING_EMPLOYEE_RESPONSE, 400)));

        // expect
        assertThatThrownBy(() -> employeeService.deleteEmployee(404L))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("generateSearchTestData")
    void can_search_employee_by_name(String search, String[] matches) {
        // given
        stubFor(get("/employees").willReturn(okJson(ALL_EMPLOYEES_RESPONSE)));

        // when
        List<Employee> response = employeeService.getEmployeesByNameSearch(search);

        // then
        assertThat(response.stream().map(Employee::name)).containsExactly(matches);
    }

    public static Stream<Arguments> generateSearchTestData() {
        return Stream.of(
                Arguments.of(null, NO_MATCHES),
                Arguments.of("", array("Foo Nixon",
                        "Garrett Foo",
                        "sammy tWo $hoes 👞",
                        "Susie Solis",
                        "Zaara Wall",
                        "Edith Huber",
                        "Yasmine Cruz",
                        "Luc Christian",
                        "Nellie Maddox",
                        "Willie Washington",
                        "Sonny Giles",
                        "Melisa Ferrell")),
                Arguments.of("can't find me", NO_MATCHES),
                Arguments.of("Nixon", array("Foo Nixon")),
                Arguments.of("niXoN", array("Foo Nixon")),
                Arguments.of("foo", array("Foo Nixon", "Garrett Foo")),
                Arguments.of("$hoes 👞", array("sammy tWo $hoes 👞"))
        );
    }

    @Test
    void can_return_highest_earning_employee() {
        // given
        stubFor(get("/employees").willReturn(okJson(ALL_EMPLOYEES_RESPONSE)));

        // when
        Employee response = employeeService.getHighestSalaryEmployee();

        // then
        assertThat(response.name()).isEqualTo("Foo Nixon");
    }

    @Test
    void returns_null_when_there_are_no_employees() {
        // given
        stubFor(get("/employees").willReturn(okJson("{}")));

        // when
        Employee response = employeeService.getHighestSalaryEmployee();

        // then
        assertThat(response).isNull();
    }

    @Test
    void can_return_top_ten_highest_earners() {
        // given
        stubFor(get("/employees").willReturn(okJson(ALL_EMPLOYEES_RESPONSE)));

        // when
        List<Employee> response = employeeService.getTopTenHighestEarningEmployees();

        // then
        assertThat(response.stream().map(Employee::id))
                .hasSize(10)
                .containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
    }

    @Test
    void returns_all_employees_as_highest_earners_if_under_ten() {
        // given
        stubFor(get("/employees").willReturn(okJson(TWO_EMPLOYEES_RESPONSE)));

        // when
        List<Employee> response = employeeService.getTopTenHighestEarningEmployees();

        // then
        assertThat(response.stream().map(Employee::id))
                .hasSize(2);
    }

    @Test
    void returns_empty_list_of_top_earners_if_no_employees() {
        // given
        stubFor(get("/employees").willReturn(okJson("{}")));

        // when
        List<Employee> response = employeeService.getTopTenHighestEarningEmployees();

        // then
        assertThat(response).isEmpty();
    }

    @Test
    void retries_requests_on_connection_faults() {
        // given
        stubFor(get(urlEqualTo("/employees")).inScenario("Retries for connection faults")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER))
                .willSetStateTo("Retry #1"));

        stubFor(get(urlEqualTo("/employees")).inScenario("Retries for connection faults")
                .whenScenarioStateIs("Retry #1")
                .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
                .willSetStateTo("Retry #2"));

        stubFor(get(urlEqualTo("/employees")).inScenario("Retries for connection faults")
                .whenScenarioStateIs("Retry #2")
                .willReturn(okJson(TWO_EMPLOYEES_RESPONSE))
                .willSetStateTo("Retry success"));

        // when
        assertThat(employeeService.getAllEmployees()).hasSize(2);
    }

    @Test
    void retries_requests_with_backoff_on_429() {
        // given
        stubFor(get(urlEqualTo("/employees")).inScenario("Retries for 429")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(429))
                .willSetStateTo("Retry #1"));

        stubFor(get(urlEqualTo("/employees")).inScenario("Retries for 429")
                .whenScenarioStateIs("Retry #1")
                .willReturn(aResponse().withStatus(429))
                .willSetStateTo("Retry #2"));

        stubFor(get(urlEqualTo("/employees")).inScenario("Retries for 429")
                .whenScenarioStateIs("Retry #2")
                .willReturn(okJson(TWO_EMPLOYEES_RESPONSE))
                .willSetStateTo("Retry success"));

        // when
        assertThat(employeeService.getAllEmployees()).hasSize(2);
    }

    @Test
    void api_client_throws_error_on_timeout() {
        // given
        stubFor(get(urlEqualTo("/employees"))
                .willReturn(aResponse().withFixedDelay(5000).withStatus(200).withBody("{}")));

        // expect
        assertThatThrownBy(() -> employeeService.getAllEmployees())
                .isInstanceOf(RetryableException.class)
                .hasCauseInstanceOf(SocketTimeoutException.class)
                .hasMessageContaining("Read timed out");
    }

    public static final String[] NO_MATCHES = {};

}