package com.example.employeemanagementsystem.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.example.employeemanagementsystem.dto.get.EmployeeDto;

@Component
public class EmployeeSearchCache {

    private final Map<EmployeeSearchCacheKey, Page<EmployeeDto>> cache =
            Collections.synchronizedMap(new HashMap<>());

    public Page<EmployeeDto> get(EmployeeSearchCacheKey key) {
        return cache.get(key);
    }

    public void put(EmployeeSearchCacheKey key, Page<EmployeeDto> page) {
        cache.put(key, page);
    }

    public void invalidateAll() {
        cache.clear();
    }
}
