package com.example.rqchallenge.employees.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class EmployeeApiErrorDecoder implements ErrorDecoder {

    private final ObjectMapper mapper;
    private final Default errorDecoder = new Default();

    public EmployeeApiErrorDecoder(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {

        switch (response.status()) {
            case 400 -> remapTo404IfMessageNotFound(response);
            case 429 -> throw new RetryableException(response.status(), "429 Too many requests " + response.request().url(), response.request().httpMethod(), (Long) null, response.request());
        }

        return errorDecoder.decode(methodKey, response);
    }

    private void remapTo404IfMessageNotFound(Response response) {
        var deserializedBody = deserialize(response);
        var errorMessage = deserializedBody.content().getOrDefault("message", "").toString();

        // Remap 400 error to 404
        if (errorMessage.equalsIgnoreCase("Not found record")) {
            throw new FeignException.NotFound("Not Found", response.request(), deserializedBody.bytes(), response.headers());
        }
    }

    private DeserializedBody deserialize(Response response) {
        Map<String, Object> responseBody = Collections.emptyMap();
        byte[] bytes = new byte[0];
        try (InputStream is = response.body().asInputStream()) {
            bytes = is.readAllBytes();
            responseBody = mapper.readValue(bytes, new TypeReference<>() {});
        } catch (Exception ex) {
            log.warn("Could not deserialize error body as JSON for original request for url: " + response.request().url());
        }
        return new DeserializedBody(responseBody, bytes);
    }

    private record DeserializedBody(Map<String, Object> content, byte[] bytes) {}
}
