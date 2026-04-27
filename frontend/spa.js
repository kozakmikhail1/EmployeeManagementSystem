(function () {
  const { createApp, computed, onMounted, reactive, ref } = Vue;

  function buildQuery(params) {
    const query = new URLSearchParams();
    Object.entries(params || {}).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== "") {
        query.append(key, String(value));
      }
    });
    const queryString = query.toString();
    return queryString ? "?" + queryString : "";
  }

  function apiPath(path, queryParams) {
    const base = window.API_BASE_URL || "";
    return base + path + buildQuery(queryParams);
  }

  function normalizeText(value) {
    return String(value || "").trim().toLowerCase();
  }

  function friendlyMessageFromServer(message, status) {
    const normalized = normalizeText(message);
    if (normalized.includes("already assigned")) {
      return "This user is already linked to another employee.";
    }
    if (normalized.includes("not found")) {
      return message || "Requested resource was not found.";
    }
    if (normalized.includes("duplicate") || normalized.includes("already exists")) {
      return "This value already exists. Please choose another one.";
    }
    if (status >= 500) {
      return "Server error. Please try again.";
    }
    return message || "Unexpected error";
  }

  async function readErrorMessage(response) {
    try {
      const data = await response.json();
      if (data && Array.isArray(data.details) && data.details.length > 0) {
        return data.details.map((item) => item.field + ": " + item.error).join("; ");
      }
      if (data && data.message) {
        return friendlyMessageFromServer(data.message, response.status);
      }
      return friendlyMessageFromServer("HTTP " + response.status, response.status);
    } catch (_) {
      return friendlyMessageFromServer("HTTP " + response.status, response.status);
    }
  }

  async function apiRequest(path, options, queryParams) {
    const requestOptions = Object.assign(
      {
        headers: {
          "Content-Type": "application/json"
        }
      },
      options || {}
    );

    let response;
    try {
      response = await fetch(apiPath(path, queryParams), requestOptions);
    } catch (_) {
      throw new Error("Network error. Check backend connection.");
    }

    if (!response.ok) {
      throw new Error(await readErrorMessage(response));
    }

    if (response.status === 204) {
      return null;
    }

    return response.json();
  }

  function compareValues(left, right) {
    if (left === right) {
      return 0;
    }

    if (left === null || left === undefined || left === "") {
      return -1;
    }
    if (right === null || right === undefined || right === "") {
      return 1;
    }

    if (typeof left === "number" && typeof right === "number") {
      return left - right;
    }

    if (typeof left === "boolean" && typeof right === "boolean") {
      return Number(left) - Number(right);
    }

    return String(left).localeCompare(String(right), undefined, {
      sensitivity: "base",
      numeric: true
    });
  }

  function sorted(items, sortBy, sortDir, valueBySort) {
    const copy = [...items];
    copy.sort((a, b) => {
      const result = compareValues(valueBySort(a, sortBy), valueBySort(b, sortBy));
      return sortDir === "desc" ? -result : result;
    });
    return copy;
  }

  function paged(items, page, size) {
    const safeSize = Math.max(1, Number(size) || 10);
    const totalItems = items.length;
    const totalPages = Math.max(1, Math.ceil(totalItems / safeSize));
    const safePage = Math.min(Math.max(Number(page) || 1, 1), totalPages);
    const start = (safePage - 1) * safeSize;
    const end = start + safeSize;

    return {
      items: items.slice(start, end),
      totalItems,
      totalPages,
      page: safePage
    };
  }

  function isEmailValid(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(email || "").trim());
  }

  function isPersonNameValid(value) {
    return /^[\p{L}]+(?:[ '-][\p{L}]+)*$/u.test(String(value || "").trim());
  }

  function requireNonBlank(label, value) {
    if (!String(value || "").trim()) {
      throw new Error(label + " is required");
    }
  }

  createApp({
    setup() {
      const tabs = [
        { key: "employees", label: "Employees" },
        { key: "departments", label: "Departments" },
        { key: "positions", label: "Positions" },
        { key: "users", label: "Users" },
        { key: "roles", label: "Roles" }
      ];

      const currentTab = ref("employees");
      const status = reactive({ kind: "success", message: "" });

      const employees = ref([]);
      const relationEmployees = ref([]);
      const departments = ref([]);
      const positions = ref([]);
      const users = ref([]);
      const roles = ref([]);

      const employeeSearchResult = ref(null);
      const employeeServerTotalPages = ref(1);
      const employeeServerTotalItems = ref(0);
      const employeeServerCurrentPage = ref(1);
      const departmentRows = ref([]);
      const departmentServerTotalPages = ref(1);
      const departmentServerTotalItems = ref(0);
      const departmentServerCurrentPage = ref(1);
      const positionRows = ref([]);
      const positionServerTotalPages = ref(1);
      const positionServerTotalItems = ref(0);
      const positionServerCurrentPage = ref(1);
      const userRows = ref([]);
      const userServerTotalPages = ref(1);
      const userServerTotalItems = ref(0);
      const userServerCurrentPage = ref(1);
      const roleRows = ref([]);
      const roleServerTotalPages = ref(1);
      const roleServerTotalItems = ref(0);
      const roleServerCurrentPage = ref(1);

      const selectedDepartmentEmployees = ref(null);
      const selectedPositionEmployees = ref(null);

      const employeeFilter = reactive({ minSalary: "", maxSalary: "" });
      const employeeSearch = reactive({
        departmentName: "",
        roleName: "",
        active: ""
      });

      const employeeGlobalSearch = ref("");

      const employeeFormVisible = ref(false);
      const departmentFormVisible = ref(false);
      const positionFormVisible = ref(false);
      const userFormVisible = ref(false);
      const roleFormVisible = ref(false);
      const employeeFormError = ref("");

      const departmentFilter = ref("");
      const positionFilter = ref("");
      const roleFilter = ref("");
      const userFilter = ref("");

      const listSettings = reactive({
        employees: { sortBy: "name", sortDir: "asc", page: 1, size: 10 },
        departments: { sortBy: "name", sortDir: "asc", page: 1, size: 10 },
        positions: { sortBy: "name", sortDir: "asc", page: 1, size: 10 },
        users: { sortBy: "username", sortDir: "asc", page: 1, size: 10 },
        roles: { sortBy: "name", sortDir: "asc", page: 1, size: 10 }
      });

      const employeeSortOptions = [
        { value: "name", label: "Name" },
        { value: "email", label: "Email" },
        { value: "salary", label: "Salary" },
        { value: "active", label: "Active" },
        { value: "department", label: "Department" },
        { value: "position", label: "Position" },
        { value: "user", label: "User" },
        { value: "updatedAt", label: "Updated" }
      ];

      const departmentSortOptions = [
        { value: "name", label: "Name" },
        { value: "description", label: "Description" },
        { value: "updatedAt", label: "Updated" }
      ];

      const positionSortOptions = [
        { value: "name", label: "Name" },
        { value: "minSalary", label: "Min salary" },
        { value: "maxSalary", label: "Max salary" },
        { value: "updatedAt", label: "Updated" }
      ];

      const userSortOptions = [
        { value: "username", label: "Username" },
        { value: "employee", label: "Employee" },
        { value: "rolesCount", label: "Roles count" },
        { value: "updatedAt", label: "Updated" }
      ];

      const roleSortOptions = [
        { value: "name", label: "Name" },
        { value: "updatedAt", label: "Updated" }
      ];

      const employeeForm = reactive(emptyEmployeeForm());
      const employeeUserAccount = reactive(emptyEmployeeUserAccount());
      const departmentForm = reactive(emptyDepartmentForm());
      const positionForm = reactive(emptyPositionForm());
      const roleForm = reactive(emptyRoleForm());
      const userForm = reactive(emptyUserForm());
      const originalLinkedEmployeeId = ref("");

      function emptyEmployeeForm() {
        return {
          id: "",
          firstName: "",
          lastName: "",
          email: "",
          hireDate: "",
          salary: "",
          isActive: true,
          departmentId: "",
          positionId: "",
          userId: ""
        };
      }

      function emptyDepartmentForm() {
        return {
          id: "",
          name: "",
          description: ""
        };
      }

      function emptyEmployeeUserAccount() {
        return {
          enabled: false,
          username: "",
          password: "",
          rolesId: []
        };
      }

      function emptyPositionForm() {
        return {
          id: "",
          name: "",
          description: "",
          minSalary: "",
          maxSalary: ""
        };
      }

      function emptyRoleForm() {
        return {
          id: "",
          name: ""
        };
      }

      function emptyUserForm() {
        return {
          id: "",
          username: "",
          password: "",
          rolesId: [],
          employeeId: ""
        };
      }

      function resetReactive(target, source) {
        Object.keys(target).forEach((key) => {
          target[key] = source[key];
        });
      }

      function setStatus(kind, message) {
        status.kind = kind;
        status.message = message;
        setTimeout(() => {
          status.message = "";
        }, 5000);
      }

      async function runWithStatus(action, successMessage) {
        try {
          await action();
          if (successMessage) {
            setStatus("success", successMessage);
          }
        } catch (error) {
          setStatus("error", error.message || "Unexpected error");
        }
      }

      function resetPage(tabKey) {
        listSettings[tabKey].page = 1;
      }

      function reloadTabData(tabKey, resetPageToFirst = false) {
        if (tabKey === "employees") {
          return loadEmployees(resetPageToFirst);
        }
        if (tabKey === "departments") {
          return loadDepartments(resetPageToFirst);
        }
        if (tabKey === "positions") {
          return loadPositions(resetPageToFirst);
        }
        if (tabKey === "users") {
          return loadUsers(resetPageToFirst);
        }
        if (tabKey === "roles") {
          return loadRoles(resetPageToFirst);
        }
        return Promise.resolve();
      }

      function changePage(tabKey, direction, totalPages) {
        const settings = listSettings[tabKey];
        settings.page = Math.min(Math.max(settings.page + direction, 1), totalPages);
        reloadTabData(tabKey, false);
      }

      function goToPage(tabKey, totalPages) {
        const settings = listSettings[tabKey];
        const rawPage = Number(settings.page);
        if (!Number.isFinite(rawPage)) {
          settings.page = 1;
          reloadTabData(tabKey, false);
          return;
        }
        settings.page = Math.min(Math.max(Math.trunc(rawPage), 1), totalPages);
        reloadTabData(tabKey, false);
      }

      function onListSettingsChanged(tabKey) {
        resetPage(tabKey);
        reloadTabData(tabKey, false);
      }

      function onPageSizeChanged(tabKey) {
        const settings = listSettings[tabKey];
        const rawSize = Number(settings.size);

        if (!Number.isFinite(rawSize)) {
          settings.size = 10;
        } else {
          settings.size = Math.max(1, Math.trunc(rawSize));
        }

        resetPage(tabKey);
        reloadTabData(tabKey, false);
      }

      function onTabFilterChanged(tabKey) {
        reloadTabData(tabKey, true);
      }

      async function loadReferenceData() {
        const [departmentData, positionData, roleData, userData] = await Promise.all([
          apiRequest("/api/departments"),
          apiRequest("/api/positions"),
          apiRequest("/api/roles"),
          apiRequest("/api/users")
        ]);

        departments.value = departmentData;
        positions.value = positionData;
        roles.value = roleData;
        users.value = userData;
      }

      async function refreshRelationEmployees() {
        relationEmployees.value = await apiRequest("/api/employees");
      }

      function employeeSortProperty(sortBy) {
        switch (sortBy) {
          case "name":
            return "lastName";
          case "email":
            return "email";
          case "salary":
            return "salary";
          case "active":
            return "isActive";
          case "department":
            return "department.name";
          case "position":
            return "position.name";
          case "user":
            return "user.username";
          case "updatedAt":
            return "updatedAt";
          default:
            return "lastName";
        }
      }

      async function loadEmployees(resetPageToFirst = true, showSearchCompleted = false) {
        await runWithStatus(async () => {
          if (resetPageToFirst) {
            resetPage("employees");
          }

          const settings = listSettings.employees;
          const data = await apiRequest("/api/employees/page", { method: "GET" }, {
            q: employeeGlobalSearch.value || undefined,
            departmentName: employeeSearch.departmentName || undefined,
            roleName: employeeSearch.roleName || undefined,
            active: employeeSearch.active || undefined,
            min_salary: employeeFilter.minSalary || undefined,
            max_salary: employeeFilter.maxSalary || undefined,
            page: Math.max((Number(settings.page) || 1) - 1, 0),
            size: Math.max(Number(settings.size) || 10, 1),
            sort: employeeSortProperty(settings.sortBy) + "," + settings.sortDir
          });

          employees.value = data.content || [];
          employeeServerTotalItems.value = Number(data.totalElements || 0);
          employeeServerTotalPages.value = Math.max(Number(data.totalPages || 1), 1);
          employeeServerCurrentPage.value = Math.max(Number(data.number || 0) + 1, 1);
          settings.page = employeeServerCurrentPage.value;

          const hasSearchCriteria = normalizeText(employeeGlobalSearch.value)
            || normalizeText(employeeSearch.departmentName)
            || normalizeText(employeeSearch.roleName)
            || employeeSearch.active !== ""
            || employeeFilter.minSalary !== ""
            || employeeFilter.maxSalary !== "";
          if (hasSearchCriteria || showSearchCompleted) {
            employeeSearchResult.value = { totalElements: employeeServerTotalItems.value };
          } else {
            employeeSearchResult.value = null;
          }
        }, showSearchCompleted ? "Search completed" : undefined);
      }

      function departmentSortProperty(sortBy) {
        if (sortBy === "description") {
          return "description";
        }
        if (sortBy === "updatedAt") {
          return "updatedAt";
        }
        return "name";
      }

      async function loadDepartments(resetPageToFirst = true) {
        await runWithStatus(async () => {
          if (resetPageToFirst) {
            resetPage("departments");
          }
          const settings = listSettings.departments;
          const data = await apiRequest("/api/departments/page", { method: "GET" }, {
            q: departmentFilter.value || undefined,
            page: Math.max((Number(settings.page) || 1) - 1, 0),
            size: Math.max(Number(settings.size) || 10, 1),
            sort: departmentSortProperty(settings.sortBy) + "," + settings.sortDir
          });
          departmentRows.value = data.content || [];
          departmentServerTotalItems.value = Number(data.totalElements || 0);
          departmentServerTotalPages.value = Math.max(Number(data.totalPages || 1), 1);
          departmentServerCurrentPage.value = Math.max(Number(data.number || 0) + 1, 1);
          settings.page = departmentServerCurrentPage.value;
        });
      }

      function positionSortProperty(sortBy) {
        if (sortBy === "minSalary") {
          return "minSalary";
        }
        if (sortBy === "maxSalary") {
          return "maxSalary";
        }
        if (sortBy === "updatedAt") {
          return "updatedAt";
        }
        return "name";
      }

      async function loadPositions(resetPageToFirst = true) {
        await runWithStatus(async () => {
          if (resetPageToFirst) {
            resetPage("positions");
          }
          const settings = listSettings.positions;
          const data = await apiRequest("/api/positions/page", { method: "GET" }, {
            q: positionFilter.value || undefined,
            page: Math.max((Number(settings.page) || 1) - 1, 0),
            size: Math.max(Number(settings.size) || 10, 1),
            sort: positionSortProperty(settings.sortBy) + "," + settings.sortDir
          });
          positionRows.value = data.content || [];
          positionServerTotalItems.value = Number(data.totalElements || 0);
          positionServerTotalPages.value = Math.max(Number(data.totalPages || 1), 1);
          positionServerCurrentPage.value = Math.max(Number(data.number || 0) + 1, 1);
          settings.page = positionServerCurrentPage.value;
        });
      }

      function userSortProperty(sortBy) {
        if (sortBy === "updatedAt") {
          return "updatedAt";
        }
        if (sortBy === "employee") {
          return "employee.id";
        }
        if (sortBy === "rolesCount") {
          return "id";
        }
        return "username";
      }

      async function loadUsers(resetPageToFirst = true) {
        await runWithStatus(async () => {
          if (resetPageToFirst) {
            resetPage("users");
          }
          const settings = listSettings.users;
          const data = await apiRequest("/api/users/page", { method: "GET" }, {
            q: userFilter.value || undefined,
            page: Math.max((Number(settings.page) || 1) - 1, 0),
            size: Math.max(Number(settings.size) || 10, 1),
            sort: userSortProperty(settings.sortBy) + "," + settings.sortDir
          });
          userRows.value = data.content || [];
          userServerTotalItems.value = Number(data.totalElements || 0);
          userServerTotalPages.value = Math.max(Number(data.totalPages || 1), 1);
          userServerCurrentPage.value = Math.max(Number(data.number || 0) + 1, 1);
          settings.page = userServerCurrentPage.value;
        });
      }

      function roleSortProperty(sortBy) {
        if (sortBy === "updatedAt") {
          return "updatedAt";
        }
        return "name";
      }

      async function loadRoles(resetPageToFirst = true) {
        await runWithStatus(async () => {
          if (resetPageToFirst) {
            resetPage("roles");
          }
          const settings = listSettings.roles;
          const data = await apiRequest("/api/roles/page", { method: "GET" }, {
            q: roleFilter.value || undefined,
            page: Math.max((Number(settings.page) || 1) - 1, 0),
            size: Math.max(Number(settings.size) || 10, 1),
            sort: roleSortProperty(settings.sortBy) + "," + settings.sortDir
          });
          roleRows.value = data.content || [];
          roleServerTotalItems.value = Number(data.totalElements || 0);
          roleServerTotalPages.value = Math.max(Number(data.totalPages || 1), 1);
          roleServerCurrentPage.value = Math.max(Number(data.number || 0) + 1, 1);
          settings.page = roleServerCurrentPage.value;
        });
      }

      async function applyEmployeeFiltersSearch() {
        await loadEmployees(true, true);
      }

      function resetEmployeeFiltersSearch() {
        employeeGlobalSearch.value = "";
        employeeFilter.minSalary = "";
        employeeFilter.maxSalary = "";
        employeeSearch.departmentName = "";
        employeeSearch.roleName = "";
        employeeSearch.active = "";
        employeeSearchResult.value = null;
        loadEmployees(true);
      }

      function resetEmployeeForm() {
        resetReactive(employeeForm, emptyEmployeeForm());
        resetReactive(employeeUserAccount, emptyEmployeeUserAccount());
        employeeFormError.value = "";
      }

      function resetDepartmentForm() {
        resetReactive(departmentForm, emptyDepartmentForm());
      }

      function resetPositionForm() {
        resetReactive(positionForm, emptyPositionForm());
      }

      function resetRoleForm() {
        resetReactive(roleForm, emptyRoleForm());
      }

      function resetUserForm() {
        resetReactive(userForm, emptyUserForm());
        originalLinkedEmployeeId.value = "";
      }

      function openEmployeeCreate() {
        resetEmployeeForm();
        employeeFormVisible.value = true;
      }

      function openDepartmentCreate() {
        resetDepartmentForm();
        departmentFormVisible.value = true;
      }

      function openPositionCreate() {
        resetPositionForm();
        positionFormVisible.value = true;
      }

      function openUserCreate() {
        resetUserForm();
        userFormVisible.value = true;
      }

      function openRoleCreate() {
        resetRoleForm();
        roleFormVisible.value = true;
      }

      function toOptionalNumber(value) {
        if (value === "" || value === null || value === undefined) {
          return null;
        }
        return Number(value);
      }

      function validateEmployeeFormPayload(payload) {
        requireNonBlank("First name", payload.firstName);
        requireNonBlank("Last name", payload.lastName);
        if (!isPersonNameValid(payload.firstName)) {
          throw new Error("First name can contain only letters, spaces, apostrophes, and hyphens");
        }
        if (!isPersonNameValid(payload.lastName)) {
          throw new Error("Last name can contain only letters, spaces, apostrophes, and hyphens");
        }
        requireNonBlank("Email", payload.email);
        if (!isEmailValid(payload.email)) {
          throw new Error("Email format is invalid");
        }
        requireNonBlank("Hire date", payload.hireDate);
        if (!Number.isFinite(payload.salary) || payload.salary <= 0) {
          throw new Error("Salary must be greater than 0");
        }
        if (!Number.isFinite(payload.departmentId) || payload.departmentId <= 0) {
          throw new Error("Department is required");
        }
        if (!Number.isFinite(payload.positionId) || payload.positionId <= 0) {
          throw new Error("Position is required");
        }
      }

      function validateEmployeeUserAccount() {
        requireNonBlank("Username", employeeUserAccount.username);
        if (String(employeeUserAccount.username).trim().length < 4) {
          throw new Error("Username must be at least 4 characters");
        }
        requireNonBlank("Password", employeeUserAccount.password);
        if (String(employeeUserAccount.password).length < 8) {
          throw new Error("Password must be at least 8 characters");
        }
      }

      function employeePayload() {
        return {
          firstName: String(employeeForm.firstName || "").trim(),
          lastName: String(employeeForm.lastName || "").trim(),
          email: String(employeeForm.email || "").trim(),
          hireDate: employeeForm.hireDate,
          salary: Number(employeeForm.salary),
          isActive: employeeForm.isActive === true || employeeForm.isActive === "true",
          departmentId: Number(employeeForm.departmentId),
          positionId: Number(employeeForm.positionId),
          userId: toOptionalNumber(employeeForm.userId)
        };
      }

      async function submitEmployeeForm() {
        employeeFormError.value = "";
        try {
          const payload = employeePayload();
          validateEmployeeFormPayload(payload);

          if (employeeForm.id) {
            await apiRequest("/api/employees/" + employeeForm.id, {
              method: "PUT",
              body: JSON.stringify(payload)
            });
          } else if (employeeUserAccount.enabled) {
            validateEmployeeUserAccount();
            const employeeWithUserPayload = {
              employeeCreateDto: {
                ...payload,
                userId: null
              },
              userCreateDto: {
                username: String(employeeUserAccount.username).trim(),
                password: employeeUserAccount.password,
                rolesId: employeeUserAccount.rolesId.map((id) => Number(id))
              }
            };
            await apiRequest("/api/employees/user", {
              method: "POST",
              body: JSON.stringify(employeeWithUserPayload)
            });
          } else {
            await apiRequest("/api/employees", {
              method: "POST",
              body: JSON.stringify(payload)
            });
          }

          resetEmployeeForm();
          employeeFormVisible.value = false;
          await Promise.all([loadReferenceData(), loadEmployees(), refreshRelationEmployees()]);
          setStatus("success", "Employee saved");
        } catch (error) {
          const message = error.message || "Unexpected error";
          employeeFormError.value = message;
          setStatus("error", message);
        }
      }

      function editEmployee(employee) {
        employeeFormVisible.value = true;
        employeeFormError.value = "";
        resetReactive(employeeUserAccount, emptyEmployeeUserAccount());
        employeeForm.id = String(employee.id);
        employeeForm.firstName = employee.firstName;
        employeeForm.lastName = employee.lastName;
        employeeForm.email = employee.email;
        employeeForm.hireDate = employee.hireDate;
        employeeForm.salary = String(employee.salary);
        employeeForm.isActive = employee.isActive;
        employeeForm.departmentId = employee.departmentId ? String(employee.departmentId) : "";
        employeeForm.positionId = employee.positionId ? String(employee.positionId) : "";
        employeeForm.userId = employee.userId ? String(employee.userId) : "";
      }

      async function deleteEmployee(id) {
        if (!window.confirm("Delete employee?")) {
          return;
        }
        await runWithStatus(async () => {
          await apiRequest("/api/employees/" + id, { method: "DELETE" });
          await Promise.all([loadEmployees(), loadReferenceData(), refreshRelationEmployees()]);
        }, "Employee deleted");
      }

      function validateDepartmentFormPayload() {
        requireNonBlank("Department name", departmentForm.name);
      }

      async function submitDepartmentForm() {
        await runWithStatus(async () => {
          validateDepartmentFormPayload();
          const payload = {
            name: String(departmentForm.name).trim(),
            description: departmentForm.description ? String(departmentForm.description).trim() : null
          };

          if (departmentForm.id) {
            await apiRequest("/api/departments/" + departmentForm.id, {
              method: "PUT",
              body: JSON.stringify(payload)
            });
          } else {
            await apiRequest("/api/departments", {
              method: "POST",
              body: JSON.stringify(payload)
            });
          }

          resetDepartmentForm();
          departmentFormVisible.value = false;
          await Promise.all([
            loadReferenceData(),
            loadEmployees(false),
            loadDepartments(false),
            refreshRelationEmployees()
          ]);
        }, "Department saved");
      }

      function editDepartment(department) {
        departmentFormVisible.value = true;
        departmentForm.id = String(department.id);
        departmentForm.name = department.name;
        departmentForm.description = department.description || "";
      }

      async function deleteDepartment(id) {
        if (!window.confirm("Delete department?")) {
          return;
        }
        await runWithStatus(async () => {
          await apiRequest("/api/departments/" + id, { method: "DELETE" });
          selectedDepartmentEmployees.value = null;
          await Promise.all([
            loadReferenceData(),
            loadEmployees(false),
            loadDepartments(false),
            refreshRelationEmployees()
          ]);
        }, "Department deleted");
      }

      async function loadDepartmentEmployees(department) {
        await runWithStatus(async () => {
          const items = await apiRequest("/api/departments/" + department.id + "/employees");
          selectedDepartmentEmployees.value = {
            name: department.name,
            employees: items
          };
        });
      }

      function validatePositionFormPayload() {
        requireNonBlank("Position name", positionForm.name);
        if (positionForm.minSalary !== "" && Number(positionForm.minSalary) < 0) {
          throw new Error("Min salary cannot be negative");
        }
        if (positionForm.maxSalary !== "" && Number(positionForm.maxSalary) < 0) {
          throw new Error("Max salary cannot be negative");
        }
        if (
          positionForm.minSalary !== ""
          && positionForm.maxSalary !== ""
          && Number(positionForm.minSalary) > Number(positionForm.maxSalary)
        ) {
          throw new Error("Min salary cannot be greater than max salary");
        }
      }

      async function submitPositionForm() {
        await runWithStatus(async () => {
          validatePositionFormPayload();
          const payload = {
            name: String(positionForm.name).trim(),
            description: positionForm.description ? String(positionForm.description).trim() : null,
            minSalary: positionForm.minSalary === "" ? null : Number(positionForm.minSalary),
            maxSalary: positionForm.maxSalary === "" ? null : Number(positionForm.maxSalary)
          };

          if (positionForm.id) {
            await apiRequest("/api/positions/" + positionForm.id, {
              method: "PUT",
              body: JSON.stringify(payload)
            });
          } else {
            await apiRequest("/api/positions", {
              method: "POST",
              body: JSON.stringify(payload)
            });
          }

          resetPositionForm();
          positionFormVisible.value = false;
          await Promise.all([
            loadReferenceData(),
            loadEmployees(false),
            loadPositions(false),
            refreshRelationEmployees()
          ]);
        }, "Position saved");
      }

      function editPosition(position) {
        positionFormVisible.value = true;
        positionForm.id = String(position.id);
        positionForm.name = position.name;
        positionForm.description = position.description || "";
        positionForm.minSalary = position.minSalary != null ? String(position.minSalary) : "";
        positionForm.maxSalary = position.maxSalary != null ? String(position.maxSalary) : "";
      }

      async function deletePosition(id) {
        if (!window.confirm("Delete position?")) {
          return;
        }
        await runWithStatus(async () => {
          await apiRequest("/api/positions/" + id, { method: "DELETE" });
          selectedPositionEmployees.value = null;
          await Promise.all([
            loadReferenceData(),
            loadEmployees(false),
            loadPositions(false),
            refreshRelationEmployees()
          ]);
        }, "Position deleted");
      }

      async function loadPositionEmployees(position) {
        await runWithStatus(async () => {
          const items = await apiRequest("/api/positions/" + position.id + "/employees");
          selectedPositionEmployees.value = {
            name: position.name,
            employees: items
          };
        });
      }

      function validateRoleFormPayload() {
        requireNonBlank("Role name", roleForm.name);
      }

      async function submitRoleForm() {
        await runWithStatus(async () => {
          validateRoleFormPayload();
          const payload = { name: String(roleForm.name).trim() };
          if (roleForm.id) {
            await apiRequest("/api/roles/" + roleForm.id, {
              method: "PUT",
              body: JSON.stringify(payload)
            });
          } else {
            await apiRequest("/api/roles", {
              method: "POST",
              body: JSON.stringify(payload)
            });
          }
          resetRoleForm();
          roleFormVisible.value = false;
          await Promise.all([loadReferenceData(), loadRoles(false)]);
        }, "Role saved");
      }

      function editRole(role) {
        roleFormVisible.value = true;
        roleForm.id = String(role.id);
        roleForm.name = role.name;
      }

      async function deleteRole(id) {
        if (!window.confirm("Delete role?")) {
          return;
        }
        await runWithStatus(async () => {
          await apiRequest("/api/roles/" + id, { method: "DELETE" });
          await Promise.all([loadReferenceData(), loadRoles(false)]);
        }, "Role deleted");
      }

      function validateUserFormPayload() {
        requireNonBlank("Username", userForm.username);
        if (String(userForm.username).trim().length < 4) {
          throw new Error("Username must be at least 4 characters");
        }
        if (!userForm.id && String(userForm.password || "").length < 8) {
          throw new Error("Password must be at least 8 characters");
        }
      }

      function employeeByUserId(userId) {
        return relationEmployees.value.find((employee) => employee.userId === userId) || null;
      }

      async function linkUserToEmployee(employeeId, userId) {
        await apiRequest("/api/employees/" + employeeId, {
          method: "PATCH",
          body: JSON.stringify({ userId })
        });
      }

      async function unlinkUserFromEmployee(employeeId) {
        await apiRequest("/api/employees/" + employeeId + "/user", {
          method: "DELETE"
        });
      }

      async function submitUserForm() {
        await runWithStatus(async () => {
          validateUserFormPayload();

          const payload = {
            username: String(userForm.username).trim(),
            rolesId: userForm.rolesId.map((id) => Number(id))
          };

          let savedUser;
          if (userForm.id) {
            if (userForm.password) {
              payload.password = userForm.password;
            }
            savedUser = await apiRequest("/api/users/" + userForm.id, {
              method: "PATCH",
              body: JSON.stringify(payload)
            });
          } else {
            payload.password = userForm.password;
            savedUser = await apiRequest("/api/users", {
              method: "POST",
              body: JSON.stringify(payload)
            });
          }

          const oldEmployeeId = originalLinkedEmployeeId.value;
          const newEmployeeId = userForm.employeeId;

          if (oldEmployeeId && !newEmployeeId) {
            await unlinkUserFromEmployee(oldEmployeeId);
          } else if (oldEmployeeId && newEmployeeId && oldEmployeeId !== newEmployeeId) {
            await unlinkUserFromEmployee(oldEmployeeId);
            await linkUserToEmployee(newEmployeeId, savedUser.id);
          } else if (!oldEmployeeId && newEmployeeId) {
            await linkUserToEmployee(newEmployeeId, savedUser.id);
          }

          resetUserForm();
          userFormVisible.value = false;
          await Promise.all([
            loadReferenceData(),
            loadEmployees(false),
            loadUsers(false),
            refreshRelationEmployees()
          ]);
        }, "User saved");
      }

      function editUser(user) {
        userFormVisible.value = true;
        userForm.id = String(user.id);
        userForm.username = user.username;
        userForm.password = "";
        userForm.rolesId = (user.roles || []).map((role) => String(role.id));
        const linkedEmployee = employeeByUserId(user.id);
        userForm.employeeId = linkedEmployee ? String(linkedEmployee.id) : "";
        originalLinkedEmployeeId.value = userForm.employeeId;
      }

      async function deleteUser(id) {
        if (!window.confirm("Delete user?")) {
          return;
        }
        await runWithStatus(async () => {
          await apiRequest("/api/users/" + id, { method: "DELETE" });
          await Promise.all([
            loadReferenceData(),
            loadEmployees(false),
            loadUsers(false),
            refreshRelationEmployees()
          ]);
        }, "User deleted");
      }

      async function unlinkUserFromUser(user) {
        const linkedEmployee = employeeByUserId(user.id);
        if (!linkedEmployee) {
          setStatus("error", "This user is not linked to an employee");
          return;
        }

        if (!window.confirm("Unlink user from employee profile?")) {
          return;
        }

        await runWithStatus(async () => {
          await unlinkUserFromEmployee(linkedEmployee.id);
          await Promise.all([loadEmployees(), refreshRelationEmployees()]);
        }, "User unlinked from employee");
      }

      function departmentNameById(id) {
        if (!id) {
          return "-";
        }
        const item = departments.value.find((department) => department.id === id);
        return item ? item.name : "Unknown";
      }

      function positionNameById(id) {
        if (!id) {
          return "-";
        }
        const item = positions.value.find((position) => position.id === id);
        return item ? item.name : "Unknown";
      }

      function usernameById(id) {
        if (!id) {
          return "-";
        }
        const item = users.value.find((user) => user.id === id);
        return item ? item.username : "Unknown";
      }

      function linkedEmployeeNameByUserId(userId) {
        const item = employeeByUserId(userId);
        if (!item) {
          return "-";
        }
        return item.firstName + " " + item.lastName;
      }

      const visibleEmployees = computed(() => employees.value);
      const employeeTotalPages = computed(() => employeeServerTotalPages.value);
      const employeeCurrentPage = computed(() => employeeServerCurrentPage.value);
      const employeeTotalItems = computed(() => employeeServerTotalItems.value);
      const filteredDepartments = computed(() => departmentRows.value);
      const departmentTotalPages = computed(() => departmentServerTotalPages.value);
      const departmentCurrentPage = computed(() => departmentServerCurrentPage.value);
      const departmentTotalItems = computed(() => departmentServerTotalItems.value);

      const filteredPositions = computed(() => positionRows.value);
      const positionTotalPages = computed(() => positionServerTotalPages.value);
      const positionCurrentPage = computed(() => positionServerCurrentPage.value);
      const positionTotalItems = computed(() => positionServerTotalItems.value);

      const filteredUsers = computed(() => userRows.value);
      const userTotalPages = computed(() => userServerTotalPages.value);
      const userCurrentPage = computed(() => userServerCurrentPage.value);
      const userTotalItems = computed(() => userServerTotalItems.value);

      const filteredRoles = computed(() => roleRows.value);
      const roleTotalPages = computed(() => roleServerTotalPages.value);
      const roleCurrentPage = computed(() => roleServerCurrentPage.value);
      const roleTotalItems = computed(() => roleServerTotalItems.value);

      const linkableEmployeesForUser = computed(() => {
        if (!userForm.id) {
          return relationEmployees.value.filter((employee) => !employee.userId);
        }

        const userId = Number(userForm.id);
        const linkedEmployee = employeeByUserId(userId);
        if (linkedEmployee) {
          return [linkedEmployee].concat(
            relationEmployees.value.filter((employee) => !employee.userId)
          );
        }

        return relationEmployees.value.filter((employee) => !employee.userId);
      });

      async function init() {
        await runWithStatus(async () => {
          await Promise.all([
            loadReferenceData(),
            loadEmployees(),
            loadDepartments(),
            loadPositions(),
            loadUsers(),
            loadRoles(),
            refreshRelationEmployees()
          ]);
        }, "Data loaded");
      }

      onMounted(() => {
        init();
      });

      return {
        tabs,
        currentTab,
        status,

        employees,
        departments,
        positions,
        users,
        roles,

        employeeFilter,
        employeeSearch,
        employeeSearchResult,
        employeeGlobalSearch,
        employeeFormVisible,
        employeeFormError,
        departmentFormVisible,
        positionFormVisible,
        userFormVisible,
        roleFormVisible,

        employeeForm,
        employeeUserAccount,
        departmentForm,
        positionForm,
        roleForm,
        userForm,

        listSettings,
        employeeSortOptions,
        departmentSortOptions,
        positionSortOptions,
        userSortOptions,
        roleSortOptions,

        departmentFilter,
        positionFilter,
        roleFilter,
        userFilter,

        selectedDepartmentEmployees,
        selectedPositionEmployees,

        visibleEmployees,
        filteredDepartments,
        filteredPositions,
        filteredRoles,
        filteredUsers,
        linkableEmployeesForUser,

        employeeTotalPages,
        employeeCurrentPage,
        employeeTotalItems,
        departmentTotalPages,
        departmentCurrentPage,
        departmentTotalItems,
        positionTotalPages,
        positionCurrentPage,
        positionTotalItems,
        userTotalPages,
        userCurrentPage,
        userTotalItems,
        roleTotalPages,
        roleCurrentPage,
        roleTotalItems,

        resetPage,
        changePage,
        goToPage,
        onListSettingsChanged,
        onPageSizeChanged,
        onTabFilterChanged,

        loadEmployees,
        openEmployeeCreate,
        openDepartmentCreate,
        openPositionCreate,
        openUserCreate,
        openRoleCreate,
        applyEmployeeFiltersSearch,
        resetEmployeeFiltersSearch,

        submitEmployeeForm,
        editEmployee,
        deleteEmployee,
        resetEmployeeForm,

        submitDepartmentForm,
        editDepartment,
        deleteDepartment,
        resetDepartmentForm,
        loadDepartmentEmployees,

        submitPositionForm,
        editPosition,
        deletePosition,
        resetPositionForm,
        loadPositionEmployees,

        submitRoleForm,
        editRole,
        deleteRole,
        resetRoleForm,

        submitUserForm,
        editUser,
        deleteUser,
        unlinkUserFromUser,
        resetUserForm,

        departmentNameById,
        positionNameById,
        usernameById,
        linkedEmployeeNameByUserId
      };
    }
  }).mount("#app");
})();
