package com.example.employeemanagementsystem.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestErrorLoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestErrorLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        boolean exceptionThrown = false;
        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException | IOException | ServletException ex) {
            exceptionThrown = true;
            LOGGER.error("Unhandled exception for {} {}", request.getMethod(), request.getRequestURI(), ex);
            throw ex;
        } finally {
            int status = response.getStatus();
            if (status >= 400 && !exceptionThrown) {
                if (status >= 500) {
                    LOGGER.error("Request failed {} {} -> {}", request.getMethod(), request.getRequestURI(), status);
                } else {
                    LOGGER.warn("Request failed {} {} -> {}", request.getMethod(), request.getRequestURI(), status);
                }
            }
        }
    }
}
