package com.example.employeemanagementsystem.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.employeemanagementsystem.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    @Query(
            value = "SELECT r FROM Role r WHERE (:q = '' OR LOWER(r.name) LIKE CONCAT('%', :q, '%'))",
            countQuery =
                    "SELECT COUNT(r.id) FROM Role r "
                            + "WHERE (:q = '' OR LOWER(r.name) LIKE CONCAT('%', :q, '%'))")
    Page<Role> searchPage(@Param("q") String q, Pageable pageable);
}
