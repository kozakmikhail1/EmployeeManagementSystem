package com.example.employeemanagementsystem.repository;

import com.example.employeemanagementsystem.model.Position;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionDao extends JpaRepository<Position, Long> {
    Optional<Position> findByName(String name);
}