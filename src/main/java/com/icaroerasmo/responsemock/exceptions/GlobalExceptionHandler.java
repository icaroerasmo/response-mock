package com.icaroerasmo.responsemock.exceptions;

import com.icaroerasmo.responsemock.dtos.ErrorDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({MockResponseException.class, EndpointNotFoundException.class})
    public ResponseEntity<ErrorDTO> handleAppExceptions(RuntimeException exception) {
        log.error("Global exception handler: ", exception);
        final ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setDescription("Mock response service could not process request. Reason: %s.".formatted(exception.getMessage()));
        return ResponseEntity.badRequest().body(errorDTO);
    }
}
