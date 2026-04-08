DO $$
BEGIN
    TRUNCATE TABLE user_roles, employees, users, roles, positions, departments RESTART IDENTITY CASCADE;

    INSERT INTO departments (name, description) VALUES
    ('Engineering', 'Backend, frontend and platform development'),
    ('Human Resources', 'Hiring, onboarding and employee support'),
    ('Finance', 'Budgeting, payroll and financial reporting'),
    ('Marketing', 'Brand promotion and campaign management'),
    ('Sales', 'Lead generation and customer acquisition'),
    ('Operations', 'Business processes and internal support');

    INSERT INTO positions (name, description, min_salary, max_salary) VALUES
    ('Junior Java Developer', 'Works on internal services under supervision', 1200.00, 2200.00),
    ('Middle Java Developer', 'Builds production features and integrations', 2200.00, 3600.00),
    ('Senior Java Developer', 'Owns architecture and mentors the team', 3600.00, 5200.00),
    ('QA Engineer', 'Designs and runs functional and regression testing', 1500.00, 2800.00),
    ('HR Manager', 'Owns recruiting funnel and employee experience', 1400.00, 2600.00),
    ('Financial Analyst', 'Prepares reports and monitors budgets', 1800.00, 3200.00);

    INSERT INTO roles (name) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_MANAGER'),
    ('ROLE_HR'),
    ('ROLE_FINANCE'),
    ('ROLE_USER');

    INSERT INTO users (username, password) VALUES
    ('admin.kozak', 'admin123'),
    ('anna.hr', 'anna123'),
    ('ivan.dev', 'ivan123'),
    ('maria.qa', 'maria123'),
    ('oleg.finance', 'oleg123'),
    ('nina.sales', 'nina123'),
    ('pavel.ops', 'pavel123'),
    ('alex.support', 'alex123'),
    ('daria.recruiter', 'daria123'),
    ('sergey.analyst', 'sergey123'),
    ('elena.assistant', 'elena123');

    INSERT INTO user_roles (user_id, role_id) VALUES
    (1, 1),
    (1, 2),
    (1, 5),
    (2, 3),
    (2, 5),
    (3, 5),
    (4, 5),
    (5, 4),
    (5, 5),
    (6, 2),
    (6, 5),
    (7, 5),
    (8, 5),
    (9, 3),
    (9, 5),
    (10, 4),
    (10, 5),
    (11, 5);

    INSERT INTO employees (
        first_name,
        last_name,
        email,
        hire_date,
        salary,
        is_active,
        department_id,
        position_id,
        user_id
    ) VALUES
    ('Mikhail', 'Kozak', 'mikhail.kozak@company.com', '2022-02-14', 5100.00, true, 1, 3, 1),
    ('Anna', 'Sidorova', 'anna.sidorova@company.com', '2023-03-01', 2400.00, true, 2, 5, 2),
    ('Ivan', 'Petrov', 'ivan.petrov@company.com', '2024-01-15', 3300.00, true, 1, 2, 3),
    ('Maria', 'Kravtsova', 'maria.kravtsova@company.com', '2023-09-10', 2600.00, true, 1, 4, 4),
    ('Oleg', 'Novik', 'oleg.novik@company.com', '2021-11-20', 3100.00, true, 3, 6, 5),
    ('Nina', 'Orlova', 'nina.orlova@company.com', '2024-05-06', 2300.00, true, 5, 5, 6),
    ('Pavel', 'Moroz', 'pavel.moroz@company.com', '2022-07-18', 2100.00, false, 6, 1, 7);
END $$;
