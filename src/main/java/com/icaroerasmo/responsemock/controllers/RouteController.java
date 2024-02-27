package com.icaroerasmo.responsemock.controllers;

import com.icaroerasmo.responsemock.models.Endpoint;
import com.icaroerasmo.responsemock.services.RouteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/rt")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @RequestMapping(path = "/", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<?> save(@RequestBody Endpoint endpoint) {
        return ResponseEntity.ok(routeService.save(endpoint).getUuid());
    }

    @SneakyThrows
    @RequestMapping(path = "/run/{uuid}")
    public void execute(@PathVariable UUID uuid, HttpServletRequest request, HttpServletResponse response) {
        routeService.execute(uuid, request, response);
    }
}
