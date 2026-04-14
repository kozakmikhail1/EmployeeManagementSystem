package com.example.employeemanagementsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.employeemanagementsystem.cache.EmployeeSearchCache;
import com.example.employeemanagementsystem.dto.create.AsyncSalaryUpdateItemDto;
import com.example.employeemanagementsystem.dto.get.AsyncTaskStatusDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class AsyncSalaryUpdateServiceTest {

    @Mock
    private AsyncSalaryUpdateExecutor asyncSalaryUpdateExecutor;

    @Mock
    private EmployeeSearchCache employeeSearchCache;

    @InjectMocks
    private AsyncSalaryUpdateService asyncSalaryUpdateService;

    @Test
    void startBulkSalaryUpdateTaskReturnsTaskIdAndCompletesTask() {
        AsyncSalaryUpdateItemDto updateItemDto = new AsyncSalaryUpdateItemDto();
        updateItemDto.setEmployeeId(1L);
        updateItemDto.setSalary(BigDecimal.valueOf(1000));
        List<AsyncSalaryUpdateItemDto> updates = List.of(updateItemDto);

        when(asyncSalaryUpdateExecutor.process(updates)).thenReturn(CompletableFuture.completedFuture(1));

        String taskId = asyncSalaryUpdateService.startBulkSalaryUpdateTask(updates);
        AsyncTaskStatusDto taskStatus = asyncSalaryUpdateService.getTaskStatus(taskId);

        assertEquals(AsyncTaskStatus.COMPLETED, taskStatus.getStatus());
        assertEquals(1, taskStatus.getProcessedItems());
        assertEquals(1L, asyncSalaryUpdateService.getProcessedItemsCounter());
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void startBulkSalaryUpdateTaskHandlesAsyncFailure() {
        List<AsyncSalaryUpdateItemDto> updates = List.of(new AsyncSalaryUpdateItemDto());
        CompletableFuture<Integer> failedFuture = CompletableFuture.failedFuture(
                new IllegalArgumentException("Broken payload"));

        when(asyncSalaryUpdateExecutor.process(updates)).thenReturn(failedFuture);

        String taskId = asyncSalaryUpdateService.startBulkSalaryUpdateTask(updates);
        AsyncTaskStatusDto taskStatus = asyncSalaryUpdateService.getTaskStatus(taskId);

        assertEquals(AsyncTaskStatus.FAILED, taskStatus.getStatus());
        assertEquals("Broken payload", taskStatus.getMessage());
        verify(employeeSearchCache).invalidateAll();
    }

    @Test
    void getTaskStatusNotFoundThrows() {
        assertThrows(ResourceNotFoundException.class,
                () -> asyncSalaryUpdateService.getTaskStatus("missing-task"));
    }
}
