package com.icaroerasmo.responsemock.controllers;

import com.icaroerasmo.responsemock.services.ResponseGeneratorService;
import com.icaroerasmo.responsemock.utils.ParametersUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequiredArgsConstructor
public class MainController {

    private final ParametersUtil parametersUtil;
    private final ResponseGeneratorService responseGeneratorService;

    @SneakyThrows
    @RequestMapping(path = "/")
    public void process(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {

        final UUID requestId = UUID.randomUUID();

        log.info("Initializing request. ID: |{}|", requestId);

        final String body = parametersUtil.getValueFromRequestQueryParam("body", httpServletRequest);
        final String produces = parametersUtil.getValueFromRequestQueryParam("produces", httpServletRequest);
        final String httpStatus = parametersUtil.getValueFromRequestQueryParam("httpStatus", httpServletRequest);
        final String headers = parametersUtil.getValueFromRequestQueryParam("headers", httpServletRequest);

        responseGeneratorService.generateResponse(requestId, headers, httpStatus, produces, body, httpServletResponse);

        log.info("Finishing request. ID: |{}|", requestId);
    }
}
