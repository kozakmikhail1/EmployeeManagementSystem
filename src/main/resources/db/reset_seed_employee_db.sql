DO $$
BEGIN
    TRUNCATE TABLE user_roles, employees, users, departments, positions, roles RESTART IDENTITY CASCADE;

    INSERT INTO departments (name, description) VALUES
    ('Engineering', 'Platform and product engineering'),
    ('Product', 'Roadmap, discovery and delivery planning'),
    ('Design', 'UX research and interface design'),
    ('Data', 'Analytics, BI and data platform'),
    ('Human Resources', 'People operations, hiring and development'),
    ('Finance', 'Accounting, planning and financial control'),
    ('Sales', 'Business development and enterprise sales'),
    ('Customer Success', 'Onboarding, support and retention'),
    ('Operations', 'Internal services and business operations'),
    ('Legal', 'Compliance, contracts and risk management')
    ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;

    INSERT INTO positions (name, description, min_salary, max_salary) VALUES
    ('Junior Software Engineer', 'Entry-level software engineer', 1800.00, 3000.00),
    ('Software Engineer', 'Mid-level backend or frontend engineer', 3000.00, 5200.00),
    ('Senior Software Engineer', 'Senior engineer with system ownership', 5200.00, 8500.00),
    ('Lead Software Engineer', 'Technical lead across product area', 7000.00, 9800.00),
    ('QA Engineer', 'Manual and automation quality engineer', 2200.00, 4300.00),
    ('DevOps Engineer', 'CI/CD, cloud and reliability engineering', 4200.00, 7600.00),
    ('Product Manager', 'Product planning and prioritization', 4200.00, 7500.00),
    ('UI/UX Designer', 'Product and interface design specialist', 2600.00, 5200.00),
    ('Data Analyst', 'Business analytics and KPI reporting', 2800.00, 5000.00),
    ('Data Engineer', 'Data pipelines and warehouse engineering', 4200.00, 7600.00),
    ('HR Specialist', 'Recruitment and people operations', 2300.00, 4100.00),
    ('HR Manager', 'Leads talent and people management', 3600.00, 6200.00),
    ('Financial Analyst', 'Budgeting and financial analysis', 3000.00, 5600.00),
    ('Accountant', 'Accounting operations and reporting', 2400.00, 4300.00),
    ('Sales Executive', 'Owns outbound sales pipeline', 2500.00, 6200.00),
    ('Account Executive', 'Manages enterprise accounts and renewals', 3500.00, 7500.00),
    ('Customer Success Manager', 'Account health and adoption', 2800.00, 5600.00),
    ('Support Specialist', 'Customer incident and ticket support', 1800.00, 3500.00),
    ('Operations Manager', 'Coordinates internal operations', 3200.00, 5900.00),
    ('Legal Counsel', 'Corporate legal support and compliance', 4500.00, 8200.00),
    ('Head of Engineering', 'Department leadership and strategy', 9000.00, 13000.00),
    ('Head of Operations', 'Operations leadership and governance', 7000.00, 10500.00)
    ON CONFLICT (name) DO UPDATE SET
        description = EXCLUDED.description,
        min_salary = EXCLUDED.min_salary,
        max_salary = EXCLUDED.max_salary;

    INSERT INTO roles (name) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_MANAGER'),
    ('ROLE_HR'),
    ('ROLE_FINANCE'),
    ('ROLE_USER')
    ON CONFLICT (name) DO NOTHING;

    WITH generated AS (
        SELECT
            n,
            (ARRAY[
                'Liam','Olivia','Noah','Emma','Ethan','Ava','Mason','Sophia','Lucas','Mia',
                'James','Charlotte','Benjamin','Amelia','Henry','Harper','Alexander','Evelyn','Daniel','Abigail',
                'Michael','Emily','William','Ella','David','Scarlett','Joseph','Grace','Samuel','Chloe',
                'Jack','Victoria','Matthew','Lily','Andrew','Hannah','Anthony','Nora','Nathan','Zoey',
                'Christopher','Leah','Ryan','Aria','Gabriel','Aubrey','Dylan','Claire','Isaac','Lucy',
                'Caleb','Madison','Thomas','Penelope','Adrian','Layla','Jonathan','Riley','Aaron','Stella'
            ])[1 + ((n * 13) % 60)] AS first_name,
            (ARRAY[
                'Smith','Johnson','Williams','Brown','Jones','Garcia','Miller','Davis','Rodriguez','Martinez',
                'Hernandez','Lopez','Gonzalez','Wilson','Anderson','Thomas','Taylor','Moore','Jackson','Martin',
                'Lee','Perez','Thompson','White','Harris','Sanchez','Clark','Ramirez','Lewis','Robinson',
                'Walker','Young','Allen','King','Wright','Scott','Torres','Nguyen','Hill','Flores',
                'Green','Adams','Nelson','Baker','Hall','Rivera','Campbell','Mitchell','Carter','Roberts',
                'Gomez','Phillips','Evans','Turner','Diaz','Parker','Cruz','Edwards','Collins','Reyes'
            ])[1 + ((n * 17) % 60)] AS last_name,
            CASE
                WHEN n <= 55 THEN 'Engineering'
                WHEN n <= 74 THEN 'Product'
                WHEN n <= 86 THEN 'Design'
                WHEN n <= 104 THEN 'Data'
                WHEN n <= 122 THEN 'Human Resources'
                WHEN n <= 140 THEN 'Finance'
                WHEN n <= 160 THEN 'Sales'
                WHEN n <= 172 THEN 'Customer Success'
                WHEN n <= 177 THEN 'Operations'
                ELSE 'Legal'
            END AS department_name,
            (DATE '2016-01-15' + ((n * 37) % 3600)) AS hire_date,
            (n % 11 <> 0) AS is_active
        FROM generate_series(1, 180) AS n
    ),
    with_dept AS (
        SELECT
            g.*,
            d.id AS department_id
        FROM generated g
        JOIN departments d ON d.name = g.department_name
    ),
    with_position AS (
        SELECT
            wd.*,
            CASE wd.department_name
                WHEN 'Engineering' THEN
                    (ARRAY['Junior Software Engineer','Software Engineer','Senior Software Engineer','Lead Software Engineer','QA Engineer','DevOps Engineer'])[1 + (wd.n % 6)]
                WHEN 'Product' THEN
                    (ARRAY['Product Manager','Product Manager','Senior Software Engineer'])[1 + (wd.n % 3)]
                WHEN 'Design' THEN
                    (ARRAY['UI/UX Designer','UI/UX Designer','Product Manager'])[1 + (wd.n % 3)]
                WHEN 'Data' THEN
                    (ARRAY['Data Analyst','Data Engineer','Senior Software Engineer'])[1 + (wd.n % 3)]
                WHEN 'Human Resources' THEN
                    (ARRAY['HR Specialist','HR Manager','HR Specialist'])[1 + (wd.n % 3)]
                WHEN 'Finance' THEN
                    (ARRAY['Financial Analyst','Accountant','Financial Analyst'])[1 + (wd.n % 3)]
                WHEN 'Sales' THEN
                    (ARRAY['Sales Executive','Account Executive','Sales Executive'])[1 + (wd.n % 3)]
                WHEN 'Customer Success' THEN
                    (ARRAY['Customer Success Manager','Support Specialist','Support Specialist'])[1 + (wd.n % 3)]
                WHEN 'Operations' THEN
                    (ARRAY['Operations Manager','Head of Operations','Operations Manager'])[1 + (wd.n % 3)]
                ELSE
                    (ARRAY['Legal Counsel','Legal Counsel','Operations Manager'])[1 + (wd.n % 3)]
            END AS position_name
        FROM with_dept wd
    ),
    leadership_override AS (
        SELECT
            wp.*,
            CASE
                WHEN wp.n = 1 THEN 'Head of Engineering'
                WHEN wp.n = 2 THEN 'Head of Operations'
                ELSE wp.position_name
            END AS final_position_name
        FROM with_position wp
    ),
    employee_rows AS (
        SELECT
            lo.n,
            lo.first_name,
            lo.last_name,
            lower(lo.first_name || '.' || lo.last_name || lo.n::text || '@northbridge-tech.com') AS email,
            lo.hire_date,
            lo.is_active,
            lo.department_id,
            p.id AS position_id,
            p.min_salary,
            p.max_salary
        FROM leadership_override lo
        JOIN positions p ON p.name = lo.final_position_name
    )
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
    )
    SELECT
        er.first_name,
        er.last_name,
        er.email,
        er.hire_date,
        (
            er.min_salary
            + ((((er.n * 29) % 100)::numeric / 100) * (er.max_salary - er.min_salary))
        )::numeric(10,2) AS salary,
        er.is_active,
        er.department_id,
        er.position_id,
        NULL
    FROM employee_rows er
    ORDER BY er.n;

    INSERT INTO users (username, password)
    SELECT
        lower(e.first_name || '.' || e.last_name || e.id::text) AS username,
        'Welcome123!'
    FROM employees e
    WHERE e.id % 7 <> 0;

    UPDATE employees e
    SET user_id = u.id
    FROM users u
    WHERE u.username = lower(e.first_name || '.' || e.last_name || e.id::text);

    INSERT INTO user_roles (user_id, role_id)
    SELECT u.id, r.id
    FROM users u
    JOIN roles r ON r.name = 'ROLE_USER'
    ON CONFLICT DO NOTHING;

    INSERT INTO user_roles (user_id, role_id)
    SELECT DISTINCT u.id, r.id
    FROM users u
    JOIN employees e ON e.user_id = u.id
    JOIN departments d ON d.id = e.department_id
    JOIN roles r ON r.name = 'ROLE_HR'
    WHERE d.name = 'Human Resources'
    ON CONFLICT DO NOTHING;

    INSERT INTO user_roles (user_id, role_id)
    SELECT DISTINCT u.id, r.id
    FROM users u
    JOIN employees e ON e.user_id = u.id
    JOIN departments d ON d.id = e.department_id
    JOIN roles r ON r.name = 'ROLE_FINANCE'
    WHERE d.name = 'Finance'
    ON CONFLICT DO NOTHING;

    INSERT INTO user_roles (user_id, role_id)
    SELECT DISTINCT u.id, r.id
    FROM users u
    JOIN employees e ON e.user_id = u.id
    JOIN positions p ON p.id = e.position_id
    JOIN roles r ON r.name = 'ROLE_MANAGER'
    WHERE p.name IN (
        'Lead Software Engineer',
        'Head of Engineering',
        'Head of Operations',
        'HR Manager',
        'Operations Manager',
        'Account Executive',
        'Customer Success Manager',
        'Product Manager'
    )
    ON CONFLICT DO NOTHING;

    WITH admin_candidates AS (
        SELECT u.id
        FROM users u
        JOIN employees e ON e.user_id = u.id
        JOIN positions p ON p.id = e.position_id
        WHERE p.name IN ('Head of Engineering', 'Head of Operations')
        ORDER BY e.id
        LIMIT 2
    )
    INSERT INTO user_roles (user_id, role_id)
    SELECT ac.id, r.id
    FROM admin_candidates ac
    JOIN roles r ON r.name = 'ROLE_ADMIN'
    ON CONFLICT DO NOTHING;
END $$;
