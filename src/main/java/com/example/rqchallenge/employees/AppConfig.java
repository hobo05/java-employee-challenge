package com.example.rqchallenge.employees;

import com.example.dummy.api.EmployeeApi;
import com.example.dummy.invoker.ApiClient;
import com.example.rqchallenge.employees.client.EmployeeApiErrorDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {

    @Value("${employee.api.base-url}")
    String employeeApiBaseUrl;

    @Value("${employee.api.read-timeout-millis:60000}")
    long readTimeoutMillis;

    @Value("${employee.api.retry.period:100}")
    long retryPeriod;

    @Value("${employee.api.retry.max-period:1000}")
    long maxRetryPeriod;

    @Value("${employee.api.retry.max-retries:5}")
    int maxRetries;

    @Bean
    public ErrorDecoder errorDecoder(ObjectMapper mapper) {
        return new EmployeeApiErrorDecoder(mapper);
    }

    @Bean
    public EmployeeApi employeeApi(ErrorDecoder errorDecoder) {
        ApiClient client = new ApiClient();
        client
                .setBasePath(employeeApiBaseUrl)
                .setFeignBuilder(client.getFeignBuilder()
                        .options(new Request.Options(10_000,
                                TimeUnit.MILLISECONDS,
                                readTimeoutMillis,
                                TimeUnit.MILLISECONDS,
                                true))
                        .errorDecoder(errorDecoder)
                        .retryer(new Retryer.Default(retryPeriod, maxRetryPeriod, maxRetries)));
        return client.buildClient(EmployeeApi.class);
    }
}
