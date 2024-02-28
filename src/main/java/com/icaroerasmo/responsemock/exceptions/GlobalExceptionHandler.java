package com.icaroerasmo.responsemock.exceptions;

import com.icaroerasmo.responsemock.dtos.ErrorDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({MockResponseException.class})
    public ResponseEntity<ErrorDTO> handleMockResponseException(MockResponseException mockResponseException) {
        log.error("Global exception handler: ", mockResponseException);
        final ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setDescription("Mock response service could not process request. Reason: %s.".formatted(mockResponseException.getMessage()));
        return ResponseEntity.badRequest().body(errorDTO);
    }
}
