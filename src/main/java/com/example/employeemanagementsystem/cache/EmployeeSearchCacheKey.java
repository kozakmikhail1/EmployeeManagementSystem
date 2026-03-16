package com.example.employeemanagementsystem.cache;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.data.domain.Pageable;

public final class EmployeeSearchCacheKey {

    public enum QueryType {
        JPQL,
        NATIVE
    }

    private final QueryType queryType;
    private final String departmentName;
    private final String roleName;
    private final BigDecimal minSalary;
    private final BigDecimal maxSalary;
    private final Boolean active;
    private final int pageNumber;
    private final int pageSize;
    private final String sort;

    private EmployeeSearchCacheKey(
            QueryType queryType,
            String departmentName,
            String roleName,
            BigDecimal minSalary,
            BigDecimal maxSalary,
            Boolean active,
            int pageNumber,
            int pageSize,
            String sort) {
        this.queryType = queryType;
        this.departmentName = normalize(departmentName);
        this.roleName = normalize(roleName);
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.active = active;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sort = sort;
    }

    public static EmployeeSearchCacheKey from(
            QueryType queryType,
            String departmentName,
            String roleName,
            BigDecimal minSalary,
            BigDecimal maxSalary,
            Boolean active,
            Pageable pageable) {
        return new EmployeeSearchCacheKey(
                queryType,
                departmentName,
                roleName,
                minSalary,
                maxSalary,
                active,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().toString());
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmployeeSearchCacheKey that)) {
            return false;
        }
        return pageNumber == that.pageNumber
                && pageSize == that.pageSize
                && queryType == that.queryType
                && Objects.equals(departmentName, that.departmentName)
                && Objects.equals(roleName, that.roleName)
                && Objects.equals(minSalary, that.minSalary)
                && Objects.equals(maxSalary, that.maxSalary)
                && Objects.equals(active, that.active)
                && Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                queryType,
                departmentName,
                roleName,
                minSalary,
                maxSalary,
                active,
                pageNumber,
                pageSize,
                sort);
    }
}
