package com.icaroerasmo.responsemock.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.icaroerasmo.responsemock.models.Endpoint;

import java.io.IOException;
import java.util.Optional;

public class RouteSerializer extends StdSerializer<Endpoint.Route> {
    public RouteSerializer() {
        this(null);
    }
    public RouteSerializer(Class<Endpoint.Route> t) {
        super(t);
    }

    @Override
    public void serialize(Endpoint.Route route, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        if(Optional.ofNullable(route.getBody()).isPresent()) {
            jsonGenerator.writeStringField("body", route.getBody());
        }
        if(Optional.ofNullable(route.getProduces()).isPresent()) {
            jsonGenerator.writeStringField("produces", route.getProduces().toString());
        }
        if(Optional.ofNullable(route.getHeaders()).isPresent()) {
            jsonGenerator.writeFieldName("headers");
            jsonGenerator.writeStartArray();
            route.getHeaders().forEach((k, v) -> {
                try {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField(k, v);
                    jsonGenerator.writeEndObject();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            jsonGenerator.writeEndArray();
        }
        if(Optional.ofNullable(route.getStatus()).isPresent()) {
            jsonGenerator.writeStringField("status", route.getStatus().name());
        }
        if(Optional.ofNullable(route.getMethod()).isPresent()) {
            jsonGenerator.writeStringField("method", route.getMethod().name());
        }
        jsonGenerator.writeEndObject();
    }
}
