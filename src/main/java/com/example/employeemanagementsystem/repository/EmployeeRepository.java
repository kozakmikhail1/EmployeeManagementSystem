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
                            + "WHERE (:departmentName IS NULL "
                            + "OR LOWER(d.name) LIKE LOWER(CONCAT('%', :departmentName, '%'))) "
                            + "AND (:roleName IS NULL OR LOWER(r.name) = LOWER(:roleName)) "
                            + "AND (:minSalary IS NULL OR e.salary >= :minSalary) "
                            + "AND (:maxSalary IS NULL OR e.salary <= :maxSalary) "
                            + "AND (:active IS NULL OR e.isActive = :active)",
            countQuery =
                    "SELECT COUNT(DISTINCT e.id) FROM Employee e "
                            + "JOIN e.department d "
                            + "LEFT JOIN e.user u "
                            + "LEFT JOIN u.roles r "
                            + "WHERE (:departmentName IS NULL "
                            + "OR LOWER(d.name) LIKE LOWER(CONCAT('%', :departmentName, '%'))) "
                            + "AND (:roleName IS NULL OR LOWER(r.name) = LOWER(:roleName)) "
                            + "AND (:minSalary IS NULL OR e.salary >= :minSalary) "
                            + "AND (:maxSalary IS NULL OR e.salary <= :maxSalary) "
                            + "AND (:active IS NULL OR e.isActive = :active)")
    Page<Employee> searchWithNestedFiltersJpql(
            @Param("departmentName") String departmentName,
            @Param("roleName") String roleName,
            @Param("minSalary") BigDecimal minSalary,
            @Param("maxSalary") BigDecimal maxSalary,
            @Param("active") Boolean active,
            Pageable pageable);

    @Query(
            value =
                    "SELECT DISTINCT e.* FROM employees e "
                            + "JOIN departments d ON e.department_id = d.id "
                            + "LEFT JOIN users u ON e.user_id = u.id "
                            + "LEFT JOIN user_roles ur ON u.id = ur.user_id "
                            + "LEFT JOIN roles r ON ur.role_id = r.id "
                            + "WHERE (:departmentName IS NULL "
                            + "OR LOWER(d.name) LIKE LOWER(CONCAT('%', :departmentName, '%'))) "
                            + "AND (:roleName IS NULL OR LOWER(r.name) = LOWER(:roleName)) "
                            + "AND (:minSalary IS NULL OR e.salary >= :minSalary) "
                            + "AND (:maxSalary IS NULL OR e.salary <= :maxSalary) "
                            + "AND (:active IS NULL OR e.is_active = :active)",
            countQuery =
                    "SELECT COUNT(DISTINCT e.id) FROM employees e "
                            + "JOIN departments d ON e.department_id = d.id "
                            + "LEFT JOIN users u ON e.user_id = u.id "
                            + "LEFT JOIN user_roles ur ON u.id = ur.user_id "
                            + "LEFT JOIN roles r ON ur.role_id = r.id "
                            + "WHERE (:departmentName IS NULL "
                            + "OR LOWER(d.name) LIKE LOWER(CONCAT('%', :departmentName, '%'))) "
                            + "AND (:roleName IS NULL OR LOWER(r.name) = LOWER(:roleName)) "
                            + "AND (:minSalary IS NULL OR e.salary >= :minSalary) "
                            + "AND (:maxSalary IS NULL OR e.salary <= :maxSalary) "
                            + "AND (:active IS NULL OR e.is_active = :active)",
            nativeQuery = true)
    Page<Employee> searchWithNestedFiltersNative(
            @Param("departmentName") String departmentName,
            @Param("roleName") String roleName,
            @Param("minSalary") BigDecimal minSalary,
            @Param("maxSalary") BigDecimal maxSalary,
            @Param("active") Boolean active,
            Pageable pageable);
}
