package com.example.service;

import com.example.entity.Employee;
import com.example.exception.UserNotFoundException;
import com.example.mapper.EmployeeMapper;
import com.example.spec.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;

    @Override
    public Employee getEmployeeById(Long id) {
        Employee employee = employeeMapper.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        log.info("Employee found: id={}, name={}", employee.getId(), employee.getName());
        return employee;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeMapper.findAll();
    }

    @Override
    public void createEmployee(Employee employee) {
        employeeMapper.save(employee);
        log.info("Employee created: id={}, name={}", employee.getId(), employee.getName());
    }

    @Override
    public void updateEmployee(Long id, Employee employee) {
        getEmployeeById(id);

        Employee employeeToUpdate = Employee.builder()
                .id(id)
                .name(employee.getName())
                .build();

        employeeMapper.update(employeeToUpdate);
        log.info("Employee updated: id={}, new name={}", employeeToUpdate.getId(), employeeToUpdate.getName());
    }

    @Override
    public void deleteEmployee(Long id) {
        checkEmployeeExists(id);

        employeeMapper.deleteById(id);
        log.info("Employee deleted: id={}", id);
    }

    @Override
    public void checkEmployeeExists(Long id) {
        if (!employeeMapper.existsById(id)) {
            throw new UserNotFoundException(id);
        }
    }
}
