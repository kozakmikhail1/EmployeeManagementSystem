package com.example.employeemanagementsystem.cache;

public final class CacheNames {

    public static final String DEPARTMENT_BY_ID = "departmentById";
    public static final String DEPARTMENTS_ALL = "departmentsAll";

    public static final String POSITION_BY_ID = "positionById";
    public static final String POSITIONS_ALL = "positionsAll";

    public static final String ROLE_BY_ID = "roleById";
    public static final String ROLES_ALL = "rolesAll";
    public static final String ROLE_BY_NAME = "roleByName";

    public static final String USER_BY_ID = "userById";
    public static final String USER_BY_USERNAME = "userByUsername";
    public static final String USERS_ALL = "usersAll";

    public static final String EMPLOYEE_BY_ID = "employeeById";
    public static final String EMPLOYEES_ALL = "employeesAll";
    public static final String EMPLOYEES_BY_SALARY_RANGE = "employeesBySalaryRange";
    public static final String EMPLOYEES_BY_DEPARTMENT = "employeesByDepartment";
    public static final String EMPLOYEES_BY_POSITION = "employeesByPosition";

    public static final String[] ALL_READ_CACHES = {
        DEPARTMENT_BY_ID,
        DEPARTMENTS_ALL,
        POSITION_BY_ID,
        POSITIONS_ALL,
        ROLE_BY_ID,
        ROLES_ALL,
        ROLE_BY_NAME,
        USER_BY_ID,
        USER_BY_USERNAME,
        USERS_ALL,
        EMPLOYEE_BY_ID,
        EMPLOYEES_ALL,
        EMPLOYEES_BY_SALARY_RANGE,
        EMPLOYEES_BY_DEPARTMENT,
        EMPLOYEES_BY_POSITION
    };

    private CacheNames() {
    }
}
