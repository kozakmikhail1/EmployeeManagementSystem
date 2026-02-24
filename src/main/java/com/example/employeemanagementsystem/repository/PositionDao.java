package com.example.employeemanagementsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.employeemanagementsystem.model.Position;

@Repository
public interface PositionDao extends JpaRepository<Position, Long> {
    Optional<Position> findByName(String name);
}