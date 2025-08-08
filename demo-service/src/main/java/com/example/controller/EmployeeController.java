package com.example.controller;

import com.example.dto.response.ApiResponse;
import com.example.entity.Employee;
import com.example.spec.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/{id}")
    public ApiResponse<Employee> getEmployeeById(@PathVariable Long id) {
        return ApiResponse.success(employeeService.getEmployeeById(id));
    }

    @GetMapping
    public ApiResponse<List<Employee>> getAllEmployees() {
        return ApiResponse.success(employeeService.getAllEmployees());
    }

    @PostMapping
    public ApiResponse<Void> createEmployee(@RequestBody Employee employee) {
        employeeService.createEmployee(employee);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        employeeService.updateEmployee(id, employee);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ApiResponse.success(null);
    }
}
