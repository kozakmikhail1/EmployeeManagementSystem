package com.example.employeemanagementsystem.dto.get;

import com.example.employeemanagementsystem.service.AsyncTaskStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AsyncTaskStatusDto {
    private String taskId;
    private AsyncTaskStatus status;
    private Integer processedItems;
    private Integer totalItems;
    private String message;
}
