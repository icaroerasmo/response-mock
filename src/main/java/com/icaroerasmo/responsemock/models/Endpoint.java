package com.icaroerasmo.responsemock.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.icaroerasmo.responsemock.deserializers.RouteDeserializer;
import com.icaroerasmo.responsemock.serializers.RouteSerializer;
import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.Serializable;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
public class Endpoint implements Serializable {
    private UUID uuid;
    private Set<Route> routes;
    @Data
    @JsonSerialize(using = RouteSerializer.class)
    @JsonDeserialize(using = RouteDeserializer.class)
    public static class Route implements Serializable{
        private String body;
        private MediaType produces;
        private Duration delay;
        private Map<String, String> headers;
        private HttpStatus status;
        private HttpMethod method;
    }
}
