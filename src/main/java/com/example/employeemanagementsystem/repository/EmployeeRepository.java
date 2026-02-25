package com.example.employeemanagementsystem.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.employeemanagementsystem.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findBySalaryBetween(BigDecimal minSalary, BigDecimal maxSalary);

    List<Employee> findBySalaryGreaterThanEqual(BigDecimal minSalary);

    List<Employee> findBySalaryLessThanEqual(BigDecimal maxSalary);

    @EntityGraph("department")
    List<Employee> findByDepartmentId(Long departmentId);

    @EntityGraph("position")
    List<Employee> findByPositionId(Long positionId);
}
