package com.example.employeemanagementsystem.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.employeemanagementsystem.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @EntityGraph(attributePaths = {"employees", "employees.user"})
    Optional<Department> findWithEmployeesById(Long id);

    @Query(
            value =
                    "SELECT d FROM Department d "
                            + "WHERE (:q = '' OR LOWER(d.name) LIKE CONCAT('%', :q, '%') "
                            + "OR LOWER(COALESCE(d.description, '')) LIKE CONCAT('%', :q, '%'))",
            countQuery =
                    "SELECT COUNT(d.id) FROM Department d "
                            + "WHERE (:q = '' OR LOWER(d.name) LIKE CONCAT('%', :q, '%') "
                            + "OR LOWER(COALESCE(d.description, '')) LIKE CONCAT('%', :q, '%'))")
    Page<Department> searchPage(@Param("q") String q, Pageable pageable);
}
