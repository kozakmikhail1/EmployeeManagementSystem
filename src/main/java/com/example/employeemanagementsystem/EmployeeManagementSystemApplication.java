package com.example.employeemanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EmployeeManagementSystemApplication {

    public static void main(final String[] args) {
        SpringApplication.run(EmployeeManagementSystemApplication.class, args);
    }
}
