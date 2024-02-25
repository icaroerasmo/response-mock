package com.icaroerasmo.responsemock.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@RestController
public class MainController {
    @SneakyThrows
    @RequestMapping(path = "/")
    public void process(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
        final UUID requestId = UUID.randomUUID();

        log.info("Initializing request. ID: |{}|", requestId);

        final String body = getValueFromRequestQueryParam("body", httpServletRequest);
        final String produces = getValueFromRequestQueryParam("produces", httpServletRequest);
        final String httpStatus = getValueFromRequestQueryParam("httpStatus", httpServletRequest);
        final String headers = getValueFromRequestQueryParam("headers", httpServletRequest);

        log.info("Request ID: |{}|; Body: [{}]", requestId, body);
        log.info("Request ID: |{}|; Content Type: [{}]", requestId, produces);
        log.info("Request ID: |{}|; Http Status: [{}]", requestId, httpStatus);
        log.info("Request ID: |{}|; Headers: [{}]", requestId, headers);

        httpServletResponse.addHeader("x-mock-request-id", requestId.toString());

        if(headers != null) {
            Arrays.stream(headers.split(";")).map(h -> h.split("=")).forEach(h -> httpServletResponse.addHeader(h[0], h[1]));
        }
        if(httpStatus != null && !httpStatus.isBlank()) {
            httpServletResponse.setStatus(Integer.parseInt(httpStatus));
        }
        if(produces != null && !produces.isBlank()) {
            httpServletResponse.setContentType(produces);
        }
        if(body != null && !body.isBlank()) {
            httpServletResponse.getWriter().write(body);
        }
        httpServletResponse.getWriter().flush();

        log.info("Finishing request. ID: {}", requestId);
    }

    private static String getValueFromRequestQueryParam(String paramName, HttpServletRequest httpServletRequest) {
        return httpServletRequest.getParameter(paramName);
    }
}
