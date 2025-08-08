package com.example.spec;

import com.example.entity.Employee;

import java.util.List;

public interface EmployeeService {
    Employee getEmployeeById(Long id);
    List<Employee> getAllEmployees();
    void createEmployee(Employee employee);
    void updateEmployee(Long id, Employee employee);
    void deleteEmployee(Long id);

    void checkEmployeeExists(Long id);
}
