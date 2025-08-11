package com.example.controller;

import com.example.dto.response.ApiResponse;
import com.example.entity.Employee;
import com.example.spec.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/{id}")
    public ApiResponse<Employee> getEmployeeById(@PathVariable Long id) {
        return ApiResponse.success("Employee retrieved successfully", employeeService.getEmployeeById(id));
    }

    @GetMapping
    public ApiResponse<List<Employee>> getAllEmployees() {
        return ApiResponse.success("All employees retrieved successfully", employeeService.getAllEmployees());
    }

    @PostMapping
    public ApiResponse<Void> createEmployee(@Valid @RequestBody Employee employee) {
        employeeService.createEmployee(employee);
        return ApiResponse.success("Employee created successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employee) {
        employeeService.updateEmployee(id, employee);
        return ApiResponse.success("Employee updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ApiResponse.success("Employee deleted successfully");
    }
}
