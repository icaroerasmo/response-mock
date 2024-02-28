package com.icaroerasmo.responsemock.services;

import com.icaroerasmo.responsemock.exceptions.MockResponseException;
import com.icaroerasmo.responsemock.utils.ParametersUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ResponseGeneratorService {

    private final ParametersUtil parametersUtil;

    public void generateResponse(UUID uuid, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        final String body = parametersUtil.getValueFromRequestQueryParam("body", httpServletRequest);
        final String produces = parametersUtil.getValueFromRequestQueryParam("produces", httpServletRequest);
        final String httpStatus = parametersUtil.getValueFromRequestQueryParam("httpStatus", httpServletRequest);
        final String headers = parametersUtil.getValueFromRequestQueryParam("headers", httpServletRequest);

        generateResponse(uuid, headers, httpStatus, produces, body, httpServletResponse);
    }

    public void generateResponse(UUID requestId, String headers, String httpStatus, String produces, String body, HttpServletResponse httpServletResponse) {
        HttpStatus status = null;
        MediaType mediaType = null;
        Map<String, String> headersMap = null;
        if(headers != null && !headers.isBlank()) {
            headersMap = Arrays.stream(headers.split(";")).map(h -> h.split("=")).collect(Collectors.toMap(e -> e[0], e -> e[1]));
        }
        if(httpStatus != null && !httpStatus.isBlank()) {
            try {
                status = HttpStatus.valueOf(Integer.parseInt(httpStatus));
            } catch (IllegalArgumentException e) {
                final String message = "Invalid HTTP status: %s".formatted(httpStatus);
                log.error(message);
                throw new MockResponseException(message, e);
            }
        }
        if(produces != null && !produces.isBlank()) {
            try {
                mediaType = MediaType.parseMediaType(produces);
            } catch (IllegalArgumentException e) {
                final String message = "Invalid media type: %s".formatted(produces);
                log.error(message);
                throw new MockResponseException(message, e);
            }
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
