package com.icaroerasmo.responsemock.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ParametersUtil {
    public String getValueFromRequestQueryParam(String paramName, HttpServletRequest httpServletRequest) {
        return httpServletRequest.getParameter(paramName);
    }
}
