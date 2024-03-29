package com.icaroerasmo.responsemock.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.icaroerasmo.responsemock.models.Endpoint;
import com.icaroerasmo.responsemock.utils.ParametersUtil;
import com.icaroerasmo.responsemock.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class RouteDeserializer extends StdDeserializer<Endpoint.Route> {

    @Autowired
    private TimeUtil timeUtil;

    @Autowired
    private ParametersUtil parametersUtil;

    public RouteDeserializer() {
        this(null);
    }
    public RouteDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Endpoint.Route deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Endpoint.Route route = new Endpoint.Route();

        JsonNode bodyNode = node.get("body");
        JsonNode producesNode = node.get("produces");
        JsonNode delayNode = node.get("delay");
        JsonNode headersNode = node.get("headers");
        JsonNode statusNode = node.get("status");
        JsonNode methodNode = node.get("method");

        if(Optional.ofNullable(bodyNode).isPresent()) {
            route.setBody(bodyNode.asText());
        }
        if(Optional.ofNullable(producesNode).isPresent()) {
            route.setProduces(parametersUtil.mediaTypeParser(producesNode.asText()));
        }
        if(Optional.ofNullable(delayNode).isPresent()) {
            route.setDelay(timeUtil.parseDurationFromString(delayNode.asText()));
        }
        if(Optional.ofNullable(headersNode).isPresent()) {
            route.setHeaders(new ObjectMapper().convertValue(headersNode, Map.class));
        }
        if(Optional.ofNullable(statusNode).isPresent()) {
            route.setStatus(parametersUtil.parseStatus(statusNode.asText()));
        }
        if(Optional.ofNullable(methodNode).isPresent()) {
            route.setMethod(parametersUtil.httpMethodParser(methodNode.asText()));
        }

        return route;
    }
}
