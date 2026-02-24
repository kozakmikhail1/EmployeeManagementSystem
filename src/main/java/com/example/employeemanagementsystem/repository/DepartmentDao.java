package com.example.employeemanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.employeemanagementsystem.model.Department;

@Repository
public interface DepartmentDao extends JpaRepository<Department, Long> {

}