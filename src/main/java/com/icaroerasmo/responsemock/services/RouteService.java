package com.icaroerasmo.responsemock.services;

import com.icaroerasmo.responsemock.exceptions.MockResponseException;
import com.icaroerasmo.responsemock.models.Endpoint;
import com.icaroerasmo.responsemock.repositories.RedisRepository;
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
@Log4j2
@Service
@RequiredArgsConstructor
public class RouteService {

    private final RedisRepository redisRepository;
    private final ParametersUtil parametersUtil;
    private final ResponseGeneratorService responseGeneratorService;

    public void runtimeRoute(UUID uuid, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        responseGeneratorService.generateResponse(uuid, httpServletRequest, httpServletResponse);
    }

    public Endpoint get(UUID uuid) {
        return redisRepository.get(uuid).orElseThrow(
                () -> new MockResponseException("Could not find endpoint %s".formatted(uuid)));
    }

    public Endpoint save(final Endpoint endpoint) {
        log.info("Saving endpoint: {}", endpoint);

        mappingValidation(endpoint);

        if(Optional.ofNullable(endpoint.getUuid()).isEmpty()) {
            endpoint.setUuid(UUID.randomUUID());
            log.info("New endpoint. Saving: {}", endpoint);
            redisRepository.put(endpoint);
            log.info("New endpoint saved: {}", endpoint);
            return endpoint;
        }

        var _found = redisRepository.get(endpoint.getUuid());

        if(_found.isEmpty()) {
            log.info("Recreating endpoint which doesn't exist anymore. Updating: {}", endpoint);
            redisRepository.put(endpoint);
            log.info("Finished update: {}", endpoint);
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
        redisRepository.put(found);
        log.info("Finished update: {}", endpoint);

        return found;
    }

    public void execute(final UUID uuid, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {

        final Endpoint foundEndpoint = redisRepository.get(uuid).
                orElseThrow(() -> new MockResponseException("Could not find endpoint %s".formatted(uuid)));

        log.info("Executing route from endpoint {}", foundEndpoint.getUuid());

        final HttpMethod httpMethod = parametersUtil.httpMethodParser(httpServletRequest.getMethod());
        final Endpoint.Route route = foundEndpoint.getRoutes().stream().
                filter(r -> r.getMethod().equals(httpMethod)).
                findAny().orElseThrow(() -> new MockResponseException("There's no route configured for method %s".formatted(httpMethod)));

        responseGeneratorService.generateResponse(uuid, route.getHeaders(), route.getStatus(), route.getProduces(), route.getDelay(), route.getBody(), httpServletResponse);

        log.info("Processed response for endpoint {} and route {}", foundEndpoint.getUuid(), route);
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
}
