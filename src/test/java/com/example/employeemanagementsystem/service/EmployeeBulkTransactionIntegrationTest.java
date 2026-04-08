package com.example.employeemanagementsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.example.employeemanagementsystem.dto.create.EmployeeCreateDto;
import com.example.employeemanagementsystem.model.Department;
import com.example.employeemanagementsystem.model.Position;
import com.example.employeemanagementsystem.repository.DepartmentRepository;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.repository.PositionRepository;
import com.example.employeemanagementsystem.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:bulk_tx_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EmployeeBulkTransactionIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    @BeforeEach
    void clearData() {
        employeeRepository.deleteAll();
        userRepository.deleteAll();
        positionRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    void transactionalBulkRollbackOnFailureDatabaseStateUnchanged() {
        Department department = createDepartment();
        Position position = createPosition();

        EmployeeCreateDto first = employeeDto(department.getId(), position.getId(), null, "emp1");
        EmployeeCreateDto second = null;
        List<EmployeeCreateDto> payload = new ArrayList<>();
        payload.add(first);
        payload.add(second);

        assertThrows(IllegalArgumentException.class,
                () -> employeeService.createEmployeesBulk(payload));

        assertEquals(0L, employeeRepository.count());
    }

    @Test
    void nonTransactionalBulkKeepsFirstInsertOnFailureDatabaseStatePartial() {
        Department department = createDepartment();
        Position position = createPosition();

        EmployeeCreateDto first = employeeDto(department.getId(), position.getId(), null, "emp3");
        EmployeeCreateDto second = null;
        List<EmployeeCreateDto> payload = new ArrayList<>();
        payload.add(first);
        payload.add(second);

        assertThrows(IllegalArgumentException.class,
                () -> employeeService.createEmployeesBulkWithoutTransaction(payload));

        assertEquals(1L, employeeRepository.count());
    }

    private Department createDepartment() {
        Department department = new Department();
        department.setName("dep-" + UUID.randomUUID());
        department.setDescription("test");
        return departmentRepository.save(department);
    }

    private Position createPosition() {
        Position position = new Position();
        position.setName("pos-" + UUID.randomUUID());
        position.setDescription("test");
        position.setMinSalary(new BigDecimal("1000"));
        position.setMaxSalary(new BigDecimal("5000"));
        return positionRepository.save(position);
    }

    private EmployeeCreateDto employeeDto(Long departmentId, Long positionId, Long userId, String suffix) {
        EmployeeCreateDto dto = new EmployeeCreateDto();
        dto.setFirstName("John" + suffix);
        dto.setLastName("Doe" + suffix);
        dto.setEmail("john." + suffix + "@example.com");
        dto.setHireDate(LocalDate.of(2024, 1, 10));
        dto.setSalary(new BigDecimal("2000"));
        dto.setIsActive(true);
        dto.setDepartmentId(departmentId);
        dto.setPositionId(positionId);
        dto.setUserId(userId);
        return dto;
    }
}
