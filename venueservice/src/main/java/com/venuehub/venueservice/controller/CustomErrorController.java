package com.venuehub.venueservice.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(WebRequest webRequest) {
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());

        Map<String, Object> customErrorAttributes = new HashMap<>();
        customErrorAttributes.put("status", 500);
        customErrorAttributes.put("error", "Internal error");
        customErrorAttributes.put("message", "Oops! Something broke. Please contact us");

        HttpStatus status = getStatus(webRequest);
        return new ResponseEntity<>(customErrorAttributes, status);
    }

    private HttpStatus getStatus(WebRequest webRequest) {
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
        if (errorAttributes.containsKey("status")) {
            Integer statusCode = (Integer) errorAttributes.get("status");
            return HttpStatus.valueOf(statusCode);
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public String getErrorPath() {
        return "/error";
    }
}
