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
    private static final int CLIENT_ERROR_STATUS = 400;
    private static final int SERVER_ERROR_STATUS = 500;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String requestInfo = request.getMethod() + " " + request.getRequestURI();
        boolean exceptionThrown = false;
        try {
            filterChain.doFilter(request, response);
        } catch (IOException ex) {
            exceptionThrown = true;
            throw new IOException("I/O error while handling request " + requestInfo, ex);
        } catch (ServletException ex) {
            exceptionThrown = true;
            throw new ServletException("Servlet error while handling request " + requestInfo, ex);
        } catch (RuntimeException ex) {
            exceptionThrown = true;
            throw new RequestProcessingException(
                    "Unexpected runtime error while handling request " + requestInfo,
                    ex);
        } finally {
            int status = response.getStatus();
            if (status >= CLIENT_ERROR_STATUS && !exceptionThrown) {
                if (status >= SERVER_ERROR_STATUS) {
                    LOGGER.error("Request failed {} -> {}", requestInfo, status);
                } else {
                    LOGGER.warn("Request failed {} -> {}", requestInfo, status);
                }
            }
        }
    }
}
