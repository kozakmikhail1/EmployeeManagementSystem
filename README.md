# 👥 Система управления персоналом

<div align="center">

**Employee Management System** — современное веб-приложение для автоматизации управления персоналом в организации.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9.x-red)](https://maven.apache.org/)
[![SonarCloud](https://img.shields.io/badge/SonarCloud-Quality%20Gate-brightgreen)](https://sonarcloud.io/)

</div>

---

## 📋 Содержание

- [О проекте](#-о-проекте)
- [Функциональность](#-функциональность)
- [Ролевая модель](#-ролевая-модель)
- [Технологический стек](#-технологический-стек)
- [Требования](#-требования)
- [Качество кода](#-качество-кода)

---

## 🎯 О проекте

**Employee Management System** — это корпоративная система, которая автоматизирует ключевые процессы управления персоналом. RESTful-сервис обеспечивает полный цикл работы с сотрудниками, отделами, должностями и правами доступа.

---

## ✨ Функциональность

### 👤 Управление сотрудниками
- ➕ Добавление нового сотрудника
- ✏️ Редактирование данных сотрудника
- 🚪 Увольнение сотрудника (деактивация)
- 📋 Просмотр списка сотрудников
- 📇 Просмотр детальной карточки сотрудника

### 🏢 Управление отделами
- ➕ Создание нового отдела
- ✏️ Редактирование отдела
- 🗑️ Удаление отдела (только без привязанных сотрудников)
- 📋 Просмотр списка отделов
- ℹ️ Детальная информация об отделе

### 💼 Управление должностями
- ➕ Добавление новой должности
- ✏️ Редактирование должности
- 🗑️ Удаление должности
- 📋 Просмотр списка должностей
- ℹ️ Детальная информация о должности

### 🔐 Управление пользователями и ролями
- 👤 Создание пользователя (привязка к сотруднику)
- ✏️ Редактирование пользователя
- 🗑️ Удаление пользователя
- 🎭 CRUD-операции для ролей
- 🔑 Назначение ролей пользователям

### 🖥️ SPA-клиент
- ✅ Реализован SPA-клиент (Vue 3) в `src/main/resources/static`
- 🔌 Клиент работает напрямую с REST API (`/api/*`)
- 🔁 Отображены связи:
  - `OneToMany`: сотрудники отдела и должности
  - `ManyToMany`: роли пользователей
- 🧩 Реализованы CRUD-операции для:
  - Employees
  - Departments
  - Positions
  - Users
  - Roles
- 🔎 Реализована фильтрация:
  - Сотрудники по диапазону зарплат
  - Глубокий поиск сотрудников (`departmentName`, `roleName`, `active`)
  - Локальные фильтры по имени/username для справочников

---

## 👑 Ролевая модель

| Роль | Права доступа |
|------|---------------|
| **Администратор** | ✅ Полный доступ ко всем функциям системы |
| **Сотрудник** | 👁️ Просмотр своих данных |

---

## 🛠️ Технологический стек

| Компонент | Технология |
|-----------|------------|
| **Язык программирования** | Java 21 |
| **Фреймворк** | Spring Boot 3.2.x |
| **База данных** | PostgreSQL |
| **Сборщик проекта** | Maven |
| **Архитектура** | REST API |
| **Анализ качества кода** | SonarCloud |

---

## 📦 Требования

- **Java Development Kit (JDK)** версии 21 или выше
- **PostgreSQL** 16+
- **Maven** 3.9.x (или использование встроенного wrapper)

---

## 🔍 Качество кода

Проект использует **SonarCloud** для непрерывного контроля качества кода и обеспечения высокой надежности системы.

---

## 🚀 CI/CD Pipeline

Проект использует **GitHub Actions** для автоматизации:
- ✅ **Сборка:** Maven clean verify на каждый push
- 📊 **Анализ качества:** SonarCloud автоматический анализ кода
- 🔍 **Проверки:** Компиляция, тесты, статический анализ

Результаты анализа: [SonarCloud Dashboard](https://sonarcloud.io/summary/overall?id=kozakmikhail1_EmployeeManagementSystem&branch=main)

---

## ▶️ Запуск SPA

1. Запустите Spring Boot приложение:
```bash
./mvnw spring-boot:run
```

2. Откройте в браузере:
- `http://localhost:8081/`

Клиент отдается тем же сервером и использует те же API-эндпоинты (`/api/*`), поэтому дополнительная настройка CORS не требуется.

---

<div align="center">
  
**Разработано с ❤️ для эффективного управления персоналом**

[Вернуться к началу](#-система-управления-персоналом)

</div>

## 🐳 Docker

### Сборка образа

```bash
docker build -t employee-management-system:latest .
```

### Запуск контейнера приложения

```bash
docker run --rm -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/employee_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  employee-management-system:latest
```

## 🧩 Docker Compose (приложение + PostgreSQL)

1. Создайте файл окружения:

```bash
cp .env.example .env
```

2. Поднимите сервисы:

```bash
docker compose up -d --build
```

3. Проверка health endpoint:

```bash
curl http://localhost:8081/actuator/health
```

## 🌍 Переменные окружения

Основные переменные:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`
- `SERVER_PORT` или `PORT`
- `APP_ASYNC_SALARY_UPDATE_PER_ITEM_DELAY_MS`

## ☁️ Бесплатный PaaS (Render)

### Что настроить в Render

1. Создайте `PostgreSQL` (Free plan).
2. Создайте `Web Service` из этого репозитория (Runtime: Docker).
3. В `Environment` укажите:
   - `SPRING_DATASOURCE_URL` = Internal Database URL
   - `SPRING_DATASOURCE_USERNAME` = DB user
   - `SPRING_DATASOURCE_PASSWORD` = DB password
   - `SPRING_JPA_HIBERNATE_DDL_AUTO` = `update`
4. Health Check Path:

```text
/actuator/health
```

## 🔁 CI/CD в GitHub Actions

Workflow: `.github/workflows/ci.yml`

Pipeline включает:

- сборку (`mvn verify`)
- тесты
- сборку Docker образа
- деплой (через Render Deploy Hook)
- post-deploy healthcheck

### GitHub Secrets

Добавьте в репозиторий:

- `RENDER_DEPLOY_HOOK_URL`
- `APP_HEALTHCHECK_URL` (например `https://your-app.onrender.com/actuator/health`)
- `SONAR_TOKEN` (опционально)
