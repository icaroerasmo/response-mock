package com.icaroerasmo.responsemock.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ResponseGeneratorService {
    public void generateResponse(UUID requestId, String headers, String httpStatus, String produces, String body, HttpServletResponse httpServletResponse) {
        HttpStatus status = null;
        MediaType mediaType = null;
        Map<String, String> headersMap = null;
        if(headers != null && !headers.isBlank()) {
            headersMap = Arrays.stream(headers.split(";")).map(h -> h.split("=")).collect(Collectors.toMap(e -> e[0], e -> e[1]));
        }
        if(httpStatus != null && !httpStatus.isBlank()) {
            status = HttpStatus.valueOf(Integer.parseInt(httpStatus));
        }
        if(produces != null && !produces.isBlank()) {
            mediaType = MediaType.parseMediaType(produces);
        }
        generateResponse(requestId, headersMap, status, mediaType, body, httpServletResponse);
    }
    @SneakyThrows
    public void generateResponse(UUID requestId, Map<String, String> headers, HttpStatus httpStatus, MediaType produces, String body, HttpServletResponse httpServletResponse) {

        log.info("Request ID: |{}|; Body: [{}]", requestId, body);
        log.info("Request ID: |{}|; Content Type: [{}]", requestId, produces);
        log.info("Request ID: |{}|; Http Status: [{}]", requestId, httpStatus);
        log.info("Request ID: |{}|; Headers: [{}]", requestId, headers);

        httpServletResponse.addHeader("x-mock-request-id", requestId.toString());

        if(headers != null) {
            headers.forEach((k, v) -> httpServletResponse.addHeader(k, v));
        }
        if(httpStatus != null) {
            httpServletResponse.setStatus(httpStatus.value());
        }
        if(produces != null) {
            httpServletResponse.setContentType(produces.toString());
        }
        if(body != null && !body.isBlank()) {
            httpServletResponse.getWriter().write(body);
        }
        httpServletResponse.getWriter().flush();
    }
}
