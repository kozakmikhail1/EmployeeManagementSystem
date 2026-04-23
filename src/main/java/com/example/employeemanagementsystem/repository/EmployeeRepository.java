package com.example.employeemanagementsystem.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.employeemanagementsystem.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findBySalaryBetween(BigDecimal minSalary, BigDecimal maxSalary);

    List<Employee> findBySalaryGreaterThanEqual(BigDecimal minSalary);

    List<Employee> findBySalaryLessThanEqual(BigDecimal maxSalary);

    List<Employee> findByDepartmentId(Long departmentId);

    Employee findByUserId(Long id);

    List<Employee> findAllByUserIdIn(List<Long> userIds);

    List<Employee> findByPositionId(Long positionId);

    boolean existsByPositionId(Long positionId);

    boolean existsByUserId(Long userId);

    boolean existsByUserIdAndIdNot(Long userId, Long id);

    @Query(
            value =
                    "SELECT DISTINCT e FROM Employee e "
                            + "JOIN e.department d "
                            + "LEFT JOIN e.user u "
                            + "LEFT JOIN u.roles r "
                            + "WHERE (:departmentName = '' "
                            + "OR LOWER(d.name) LIKE CONCAT('%', :departmentName, '%')) "
                            + "AND (:roleName = '' OR LOWER(r.name) = :roleName) "
                            + "AND (:active IS NULL OR e.isActive = :active)",
            countQuery =
                    "SELECT COUNT(DISTINCT e.id) FROM Employee e "
                            + "JOIN e.department d "
                            + "LEFT JOIN e.user u "
                            + "LEFT JOIN u.roles r "
                            + "WHERE (:departmentName = '' "
                            + "OR LOWER(d.name) LIKE CONCAT('%', :departmentName, '%')) "
                            + "AND (:roleName = '' OR LOWER(r.name) = :roleName) "
                            + "AND (:active IS NULL OR e.isActive = :active)")
    Page<Employee> searchWithNestedFiltersJpql(
            @Param("departmentName") String departmentName,
            @Param("roleName") String roleName,
            @Param("active") Boolean active,
            Pageable pageable);

}
