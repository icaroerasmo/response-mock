package com.icaroerasmo.responsemock.services;

import com.icaroerasmo.responsemock.utils.ParametersUtil;
import com.icaroerasmo.responsemock.utils.TimeUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class ResponseGeneratorService {

    private final ParametersUtil parametersUtil;
    private final TimeUtil timeUtil;

    public void generateResponse(UUID uuid, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        final String body = parametersUtil.getValueFromRequestQueryParam("body", httpServletRequest);
        final String produces = parametersUtil.getValueFromRequestQueryParam("produces", httpServletRequest);
        final String delay = parametersUtil.getValueFromRequestQueryParam("delay", httpServletRequest);
        final String httpStatus = parametersUtil.getValueFromRequestQueryParam("httpStatus", httpServletRequest);
        final String headers = parametersUtil.getValueFromRequestQueryParam("headers", httpServletRequest);

        generateResponse(uuid, headers, httpStatus, produces, delay, body, httpServletResponse);
    }

    public void generateResponse(
            UUID requestId, String headers,
            String httpStatus, String produces, String delay,
            String body, HttpServletResponse httpServletResponse) {
        Duration parsedDelay = null;
        HttpStatus status = null;
        MediaType mediaType = null;
        Map<String, String> headersMap = null;
        if(Optional.ofNullable(headers).isPresent() && !headers.isBlank()) {
            headersMap = parametersUtil.parseHeaders(headers);
        }
        if(Optional.ofNullable(httpStatus).isPresent() && !httpStatus.isBlank()) {
            status = parametersUtil.parseStatusFromCode(httpStatus);
        }
        if(Optional.ofNullable(produces).isPresent() && !produces.isBlank()) {
            mediaType = parametersUtil.mediaTypeParser(produces);
        }
        if(Optional.ofNullable(delay).isPresent() && !delay.isBlank()) {
            parsedDelay = timeUtil.parseDurationFromString(delay);
        }

        generateResponse(requestId, headersMap, status, mediaType, parsedDelay, body, httpServletResponse);
    }

    @SneakyThrows
    public void generateResponse(
            UUID requestId, Map<String, String> headers,
            HttpStatus httpStatus, MediaType produces, Duration delay,
            String body, HttpServletResponse httpServletResponse) {

        log.info("Request ID |{}|: Body: [{}]", requestId, body);
        log.info("Request ID |{}|: Content Type: [{}]", requestId, produces);
        log.info("Request ID |{}|: Http Status: [{}]", requestId, httpStatus);
        log.info("Request ID |{}|: Headers: [{}]", requestId, headers);
        log.info("Request ID |{}|: Delay in millis: [{}]", requestId, Optional.ofNullable(delay).isPresent() ? delay.toMillis() : null);

        if(Optional.ofNullable(delay).isPresent()) {
            log.info("Request ID |{}|: Executing {} delay in milliseconds", requestId, delay.toMillis());
            Thread.sleep(delay.toMillis());
            log.info("Request ID |{}|: Finished {} delay in milliseconds", requestId, delay.toMillis());
        }

        httpServletResponse.addHeader("x-mock-request-id", requestId.toString());

        if(Optional.ofNullable(headers).isPresent()) {
            headers.forEach((k, v) -> httpServletResponse.addHeader(k, v));
        }
        if(Optional.ofNullable(httpStatus).isPresent()) {
            httpServletResponse.setStatus(httpStatus.value());
        }
        if(Optional.ofNullable(produces).isPresent()) {
            httpServletResponse.setContentType(produces.toString());
        }
        if(Optional.ofNullable(body).isPresent() && !body.isBlank()) {
            httpServletResponse.getWriter().write(body);
        }
        httpServletResponse.getWriter().flush();
    }
}
