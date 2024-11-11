package com.np.mcdiffy.exceptionHandling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        response.setHeader("custom-header-reason", "Authentication failed");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        //If authenticationException.getMessage() != null --> then return it otherwise return "Unauthorized"
        String errorMessage = (authException!= null && authException.getMessage()!=null)
                                                        ? authException.getMessage():"Unauthorized";
        String path = request.getRequestURI();
        String jsonResponse =
                    String.format("{\n" +
                            "    \"timestamp\": \"2024-11-01T05:47:48.023+00:00\",\n" +
                            "    \"status\": 401,\n" +
                            "    \"error\": \"NP - You're Unauthorized\",\n" +
                            "    \"message\": \"%s\",\n" +
                            "    \"path\": \"%s\"\n" +
                            "}",errorMessage, path);
        response.getWriter().write(jsonResponse);
    }
}