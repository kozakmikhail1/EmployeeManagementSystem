package com.example.employeemanagementsystem.exception;

import java.util.List;

public class ApiError {

    private final String timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<ApiErrorDetail> details;

    public ApiError(
            String timestamp,
            int status,
            String error,
            String message,
            String path,
            List<ApiErrorDetail> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<ApiErrorDetail> getDetails() {
        return details;
    }
}
