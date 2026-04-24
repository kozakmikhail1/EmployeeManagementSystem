package com.example.employeemanagementsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.employeemanagementsystem.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    @EntityGraph(attributePaths = "roles")
    List<User> findAll();

    @Override
    @EntityGraph(attributePaths = "roles")
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsername(String username);

    @Query(
            value =
                    "SELECT u FROM User u "
                            + "WHERE (:q = '' OR LOWER(u.username) LIKE CONCAT('%', :q, '%'))",
            countQuery =
                    "SELECT COUNT(u.id) FROM User u "
                            + "WHERE (:q = '' OR LOWER(u.username) LIKE CONCAT('%', :q, '%'))")
    @EntityGraph(attributePaths = {"roles", "employee"})
    Page<User> searchPage(@Param("q") String q, Pageable pageable);
}
