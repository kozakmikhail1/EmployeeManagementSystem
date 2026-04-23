package com.example.employeemanagementsystem.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Caching(evict = {
    @CacheEvict(cacheNames = CacheNames.DEPARTMENT_BY_ID, allEntries = true),
    @CacheEvict(cacheNames = CacheNames.DEPARTMENTS_ALL, allEntries = true),
    @CacheEvict(cacheNames = CacheNames.POSITION_BY_ID, allEntries = true),
    @CacheEvict(cacheNames = CacheNames.POSITIONS_ALL, allEntries = true),
    @CacheEvict(cacheNames = CacheNames.ROLE_BY_ID, allEntries = true),
    @CacheEvict(cacheNames = CacheNames.ROLES_ALL, allEntries = true),
    @CacheEvict(cacheNames = CacheNames.ROLE_BY_NAME, allEntries = true),
    @CacheEvict(cacheNames = CacheNames.USER_BY_ID, allEntries = true),
    @CacheEvict(cacheNames = CacheNames.USER_BY_USERNAME, allEntries = true),
    @CacheEvict(cacheNames = CacheNames.USERS_ALL, allEntries = true),
    @CacheEvict(cacheNames = CacheNames.EMPLOYEE_BY_ID, allEntries = true),
    @CacheEvict(cacheNames = CacheNames.EMPLOYEES_ALL, allEntries = true),
    @CacheEvict(cacheNames = CacheNames.EMPLOYEES_BY_SALARY_RANGE, allEntries = true),
    @CacheEvict(cacheNames = CacheNames.EMPLOYEES_BY_DEPARTMENT, allEntries = true),
    @CacheEvict(cacheNames = CacheNames.EMPLOYEES_BY_POSITION, allEntries = true)
})
public @interface InvalidateReadCaches {
}
