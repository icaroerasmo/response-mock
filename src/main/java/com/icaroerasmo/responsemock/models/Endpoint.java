package com.icaroerasmo.responsemock.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.icaroerasmo.responsemock.serializers.HttpMethodSerializer;
import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
public class Endpoint {
    private UUID uuid;
    private Set<Route> routes;
    @Data
    @JsonSerialize(using = HttpMethodSerializer.class)
    public static class Route {
        private String body;
        private MediaType produces;
        private Map<String, String> headers;
        private HttpStatus status;
        private HttpMethod method;
    }
}
