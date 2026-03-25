package com.example.employeemanagementsystem.exception;

public class ApiErrorDetail {

    private final String field;
    private final String message;

    public ApiErrorDetail(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
