package com.icaroerasmo.responsemock.services;

import com.icaroerasmo.responsemock.models.Endpoint;
import com.icaroerasmo.responsemock.utils.ParametersUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
//TODO create specialized exceptions to each case
@Log4j2
@Service
@RequiredArgsConstructor
public class RouteService {

    private static Set<Endpoint> savedRoutes = new HashSet<>();

    private final ResponseGeneratorService responseGeneratorService;

    public Endpoint save(final Endpoint endpoint) {
        log.info("Saving endpoint: {}", endpoint);

        mappingValidation(endpoint);

        var _found = get(endpoint.getUuid());
        if(_found.isEmpty()) {
            log.info("New endpoint. Saving: {}", endpoint);
            endpoint.setUuid(UUID.randomUUID());
            savedRoutes.add(endpoint);
            log.info("New endpoint saved: {}", endpoint);
            return endpoint;
        }

        log.info("Endpoint already exists. Updating: {}", endpoint);
        final Endpoint found = _found.get();
        Set<Endpoint.Route> foundRoutes = found.getRoutes().stream().
                filter(r -> endpoint.getRoutes().stream().anyMatch(
                        r2 -> r.getMethod().equals(r2.getMethod()))).
                collect(Collectors.toSet());
        found.getRoutes().removeAll(foundRoutes);
        found.getRoutes().addAll(endpoint.getRoutes());
        log.info("Finished update: {}", endpoint);
        return found;
    }

    public void execute(final UUID uuid, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {

        final Endpoint foundEndpoint = savedRoutes.stream().
                filter(
                        e -> uuid != null &&
                                e.getUuid().equals(uuid)).findAny().
                orElseThrow(() -> new RuntimeException("Endpoint not found"));

        log.info("Executing route from endpoint {}", foundEndpoint.getUuid());

        final HttpMethod httpMethod = HttpMethod.valueOf(httpServletRequest.getMethod());
        final Endpoint.Route route = foundEndpoint.getRoutes().stream().
                filter(r -> r.getMethod().equals(httpMethod)).
                findAny().orElseThrow(() -> new RuntimeException("Route not found"));

        responseGeneratorService.generateResponse(uuid, route.getHeaders(), route.getStatus(), route.getProduces(), route.getBody(), httpServletResponse);

        log.info("Processed response for endpoint {} and route {}", foundEndpoint.getUuid(), route);
    }

    @Scheduled(cron = "${response-mock.cron.cache}")
    public void cleanCache() {
        log.warn("Cleaning cache!");
        savedRoutes.clear();
        log.warn("Cache cleaned!");
    }

    private static void mappingValidation(Endpoint endpoint) {
        var methods = endpoint.getRoutes().stream().
                collect(Collectors.
                        groupingBy(r -> r.getMethod().toString(), Collectors.counting()));

        var duplicateMappings = methods.keySet().stream().filter(r -> methods.get(r) > 1).collect(Collectors.toSet());

        if(duplicateMappings.size() > 0) {
            final String messages = duplicateMappings.stream().
                    map(dm -> "Duplicate mapping for endpoint %s and method %s".
                            formatted(endpoint.getUuid() == null ||
                                    endpoint.getUuid().toString().isBlank() ? "" : endpoint.getUuid(), dm)).
                    collect(Collectors.joining(";"));
            throw new RuntimeException(messages);
        }
    }

    public Optional<Endpoint> get(UUID uuid) {
        return savedRoutes.stream().
                filter(
                        e -> uuid != null &&
                                e.getUuid().equals(uuid)).findAny();
    }
}
