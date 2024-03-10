package com.icaroerasmo.responsemock.controllers;

import com.icaroerasmo.responsemock.exceptions.MockResponseException;
import com.icaroerasmo.responsemock.models.Endpoint;
import com.icaroerasmo.responsemock.services.RouteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Log4j2
@RestController
@RequestMapping("/rt")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @RequestMapping(path = "/", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<Endpoint> save(@RequestBody Endpoint endpoint) {
        return ResponseEntity.ok(routeService.save(endpoint));
    }

    @RequestMapping(path = "/{uuid}", method = {RequestMethod.GET})
    public ResponseEntity<Endpoint> get(@PathVariable UUID uuid) {
        return ResponseEntity.ok(routeService.get(uuid));
    }

    @SneakyThrows
    @RequestMapping(path = "/run")
    public void process(final HttpServletRequest request, final HttpServletResponse response) {
        final UUID uuid = UUID.randomUUID();
        log.info("Initializing request. ID: |{}|", uuid);
        routeService.runtimeRoute(uuid, request, response);
        log.info("Finishing request. ID: |{}|", uuid);
    }

    @SneakyThrows
    @RequestMapping(path = "/run/{uuid}")
    public void execute(@PathVariable UUID uuid, HttpServletRequest request, HttpServletResponse response) {
        log.info("Initializing request. ID: |{}|", uuid);
        routeService.execute(uuid, request, response);
        log.info("Finishing request. ID: |{}|", uuid);

    }
}
