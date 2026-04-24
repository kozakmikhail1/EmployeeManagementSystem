package com.example.employeemanagementsystem;

import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class EmployeeManagementSystemApplication {

    public static void main(final String[] args) {
        adaptRenderPostgresUrl();
        SpringApplication.run(EmployeeManagementSystemApplication.class, args);
    }

    private static void adaptRenderPostgresUrl() {
        String rawUrl = firstNonBlank(
                System.getProperty("spring.datasource.url"),
                System.getenv("SPRING_DATASOURCE_URL"),
                System.getenv("DATABASE_URL"));

        if (rawUrl == null) {
            return;
        }

        if (!rawUrl.startsWith("postgresql://") && !rawUrl.startsWith("postgres://")) {
            return;
        }

        URI uri = URI.create(rawUrl);
        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            return;
        }

        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
        String path = uri.getPath() == null ? "" : uri.getPath();
        String query = uri.getQuery();

        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
        if (query != null && !query.isBlank()) {
            jdbcUrl += "?" + query;
        }

        System.setProperty("spring.datasource.url", jdbcUrl);

        String userInfo = uri.getUserInfo();
        if (userInfo == null || userInfo.isBlank()) {
            return;
        }

        String[] parts = userInfo.split(":", 2);
        if (!parts[0].isBlank() && System.getProperty("spring.datasource.username") == null) {
            System.setProperty("spring.datasource.username", parts[0]);
        }
        if (parts.length > 1 && !parts[1].isBlank() && System.getProperty("spring.datasource.password") == null) {
            System.setProperty("spring.datasource.password", parts[1]);
        }
    }

    private static String firstNonBlank(final String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
