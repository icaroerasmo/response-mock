package com.icaroerasmo.responsemock.utils;

import com.icaroerasmo.responsemock.exceptions.MockResponseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class ParametersUtil {
    public String getValueFromRequestQueryParam(String paramName, HttpServletRequest httpServletRequest) {
        return httpServletRequest.getParameter(paramName);
    }

    public Map<String, String> parseHeaders(String headers) {
        return Arrays.stream(headers.split(";")).
                map(h -> h.split("=")).collect(Collectors.toMap(e -> e[0], e -> e[1]));
    }

    public HttpStatus parseStatus(String httpStatus) {
        HttpStatus status;
        try {
            if(httpStatus.matches("[0-9]{3}")) {
                status = HttpStatus.valueOf(Integer.parseInt(httpStatus));
            } else {
                status = HttpStatus.valueOf(httpStatus);
            }
        } catch (IllegalArgumentException e) {
            final String message = "Invalid HTTP status: %s".formatted(httpStatus);
            log.error(message);
            throw new MockResponseException(message, e);
        }
        return status;
    }

    public HttpStatus parseStatusFromCode(String httpStatus) {
        HttpStatus status;
        try {
            status = HttpStatus.valueOf(Integer.parseInt(httpStatus));
        } catch (IllegalArgumentException e) {
            final String message = "Invalid HTTP status: %s".formatted(httpStatus);
            log.error(message);
            throw new MockResponseException(message, e);
        }
        return status;
    }

    public MediaType mediaTypeParser(String produces) {
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(produces);
        } catch (IllegalArgumentException e) {
            final String message = "Invalid media type: %s".formatted(produces);
            log.error(message);
            throw new MockResponseException(message, e);
        }
        return mediaType;
    }

    public HttpMethod httpMethodParser(String method) {
        return HttpMethod.valueOf(method);
    }
}
