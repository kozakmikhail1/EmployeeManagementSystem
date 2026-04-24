package com.example.employeemanagementsystem.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.employeemanagementsystem.model.Position;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    Optional<Position> findByName(String name);

    @Query(
            value =
                    "SELECT p FROM Position p "
                            + "WHERE (:q = '' OR LOWER(p.name) LIKE CONCAT('%', :q, '%') "
                            + "OR LOWER(COALESCE(p.description, '')) LIKE CONCAT('%', :q, '%'))",
            countQuery =
                    "SELECT COUNT(p.id) FROM Position p "
                            + "WHERE (:q = '' OR LOWER(p.name) LIKE CONCAT('%', :q, '%') "
                            + "OR LOWER(COALESCE(p.description, '')) LIKE CONCAT('%', :q, '%'))")
    Page<Position> searchPage(@Param("q") String q, Pageable pageable);
}
