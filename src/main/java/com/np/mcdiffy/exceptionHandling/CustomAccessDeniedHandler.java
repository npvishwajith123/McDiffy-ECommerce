package com.np.mcdiffy.exceptionHandling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        response.setHeader("custom-header-reason", "Authorization failed");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        //If authenticationException.getMessage() != null --> then return it otherwise return "Unauthorized"
        String errorMessage = (accessDeniedException!= null && accessDeniedException.getMessage()!=null)
                ? accessDeniedException.getMessage():"Authorization failed";
        String path = request.getRequestURI();
        String jsonResponse =
                String.format("{\n" +
                        "    \"timestamp\": \"2024-11-01T05:47:48.023+00:00\",\n" +
                        "    \"status\": 403,\n" +
                        "    \"error\": \"NP - You don't have access\",\n" +
                        "    \"message\": \"%s\",\n" +
                        "    \"path\": \"%s\"\n" +
                        "}",errorMessage, path);
        response.getWriter().write(jsonResponse);
    }
}
