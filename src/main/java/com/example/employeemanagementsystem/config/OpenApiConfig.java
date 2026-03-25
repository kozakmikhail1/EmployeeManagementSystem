package com.example.employeemanagementsystem.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Employee Management System API",
                version = "v1",
                description = "REST API for managing employees, departments, positions, users, and roles"))
public class OpenApiConfig {
}
