package com.icaroerasmo.responsemock.services;

import com.icaroerasmo.responsemock.models.Endpoint;
import com.icaroerasmo.responsemock.utils.ParametersUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {

    private static Set<Endpoint> savedRoutes = new HashSet<>();

    private final ParametersUtil parametersUtil;
    private final ResponseGeneratorService responseGeneratorService;

    public Endpoint save(final Endpoint endpoint) {
        var _found = savedRoutes.stream().
                filter(
                        e -> endpoint.getUuid() != null &&
                                e.getUuid().equals(endpoint.getUuid())).findAny();
        if(_found.isEmpty()) {
            endpoint.setUuid(UUID.randomUUID());
            savedRoutes.add(endpoint);
            return endpoint;
        }

        final Endpoint found = _found.get();
        Set<Endpoint.Route> foundRoutes = found.getRoutes().stream().
                filter(r -> endpoint.getRoutes().stream().anyMatch(
                        r2 -> r.getMethod().equals(r2.getMethod()))).
                collect(Collectors.toSet());
        found.getRoutes().removeAll(foundRoutes);
        found.getRoutes().addAll(endpoint.getRoutes());

        return found;
    }

    public void execute(final UUID uuid, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {

        final Endpoint foundEndpoint = savedRoutes.stream().
                filter(
                        e -> uuid != null &&
                                e.getUuid().equals(uuid)).findAny().
                orElseThrow(() -> new RuntimeException("Endpoint not found"));

        final HttpMethod httpMethod =  HttpMethod.valueOf(httpServletRequest.getMethod());
        final Endpoint.Route route = foundEndpoint.getRoutes().stream().
                filter(r -> r.getMethod().equals(httpMethod)).
                findAny().orElseThrow(() -> new RuntimeException("Route not found"));

        responseGeneratorService.generateResponse(uuid, route.getHeaders(), route.getStatus(), route.getProduces(), route.getBody(), httpServletResponse);
    }
}
