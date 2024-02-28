package com.icaroerasmo.responsemock.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.icaroerasmo.responsemock.models.Endpoint;

import java.io.IOException;

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
        jsonGenerator.writeStringField("body", route.getBody());
        jsonGenerator.writeStringField("produces", route.getProduces().toString());
        jsonGenerator.writeFieldName("headers");
        jsonGenerator.writeStartArray();
        route.getHeaders().forEach((k,v) -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField(k, v);
                jsonGenerator.writeEndObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        jsonGenerator.writeEndArray();
        jsonGenerator.writeStringField("status", route.getStatus().name());
        jsonGenerator.writeStringField("method", route.getMethod().name());
        jsonGenerator.writeEndObject();
    }
}
