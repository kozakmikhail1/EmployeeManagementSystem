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

    @EntityGraph(attributePaths = {
        "department",
        "position",
        "user",
        "user.roles"
    })
    List<Employee> findAll();

    @EntityGraph(attributePaths = {
        "department",
        "position",
        "user.role"
    })
    Employee findByIdEmployee(Long id);

    List<Employee> findBySalaryGreaterThanEqual(BigDecimal minSalary);

    List<Employee> findBySalaryLessThanEqual(BigDecimal maxSalary);

    @EntityGraph(attributePaths = "department")
    List<Employee> findByDepartmentId(Long departmentId);

    Employee findByUserId(Long id);

    @EntityGraph("position")
    List<Employee> findByPositionId(Long positionId);
}
