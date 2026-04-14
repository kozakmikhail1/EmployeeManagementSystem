package com.example.employeemanagementsystem.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.employeemanagementsystem.cache.EmployeeSearchCache;
import com.example.employeemanagementsystem.dto.create.AsyncSalaryUpdateItemDto;
import com.example.employeemanagementsystem.dto.get.AsyncTaskStatusDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;

@Service
public class AsyncSalaryUpdateService {

    private final AsyncSalaryUpdateExecutor asyncSalaryUpdateExecutor;
    private final EmployeeSearchCache employeeSearchCache;
    private final AtomicLong processedItemsCounter = new AtomicLong();
    private final ConcurrentMap<String, AsyncTaskInfo> tasks = new ConcurrentHashMap<>();

    @Autowired
    public AsyncSalaryUpdateService(
            AsyncSalaryUpdateExecutor asyncSalaryUpdateExecutor,
            EmployeeSearchCache employeeSearchCache) {
        this.asyncSalaryUpdateExecutor = asyncSalaryUpdateExecutor;
        this.employeeSearchCache = employeeSearchCache;
    }

    public String startBulkSalaryUpdateTask(List<AsyncSalaryUpdateItemDto> updates) {
        String taskId = UUID.randomUUID().toString();
        int totalItems = updates == null ? 0 : updates.size();
        AsyncTaskInfo taskInfo = new AsyncTaskInfo(taskId, totalItems);
        tasks.put(taskId, taskInfo);
        taskInfo.setStatus(AsyncTaskStatus.RUNNING);
        taskInfo.setMessage("Task is running");
        asyncSalaryUpdateExecutor.process(updates).whenComplete((result, throwable) -> {
            if (throwable != null) {
                taskInfo.setStatus(AsyncTaskStatus.FAILED);
                taskInfo.setMessage(throwable.getCause() == null
                        ? throwable.getMessage()
                        : throwable.getCause().getMessage());
            } else {
                taskInfo.setProcessedItems(result);
                processedItemsCounter.addAndGet(result);
                taskInfo.setStatus(AsyncTaskStatus.COMPLETED);
                taskInfo.setMessage("Task completed successfully");
            }
            employeeSearchCache.invalidateAll();
        });
        return taskId;
    }

    public AsyncTaskStatusDto getTaskStatus(String taskId) {
        AsyncTaskInfo taskInfo = tasks.get(taskId);
        if (taskInfo == null) {
            throw new ResourceNotFoundException("Task not found with id " + taskId);
        }
        return taskInfo.toDto();
    }

    public long getProcessedItemsCounter() {
        return processedItemsCounter.get();
    }

    private static final class AsyncTaskInfo {
        private final String taskId;
        private final int totalItems;
        private final AtomicInteger processedItems = new AtomicInteger();
        private volatile AsyncTaskStatus status;
        private volatile String message;

        private AsyncTaskInfo(String taskId, int totalItems) {
            this.taskId = taskId;
            this.totalItems = totalItems;
            this.status = AsyncTaskStatus.PENDING;
            this.message = "Task accepted";
        }

        private void setProcessedItems(int value) {
            processedItems.set(value);
        }

        private void setStatus(AsyncTaskStatus status) {
            this.status = status;
        }

        private void setMessage(String message) {
            this.message = message;
        }

        private AsyncTaskStatusDto toDto() {
            return new AsyncTaskStatusDto(
                    taskId,
                    status,
                    processedItems.get(),
                    totalItems,
                    message);
        }
    }
}
