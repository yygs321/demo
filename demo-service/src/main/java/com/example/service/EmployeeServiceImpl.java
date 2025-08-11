package com.example.service;

import com.example.entity.Employee;
import com.example.exception.ConflictException;
import com.example.exception.NotFoundException;
import org.springframework.dao.DuplicateKeyException;
import com.example.mapper.EmployeeMapper;
import com.example.spec.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;

    @Override
    public Employee getEmployeeById(Long id) {
        Employee employee = employeeMapper.findById(id)
                .orElseThrow(NotFoundException::new);
        log.info("Employee found: id={}, name={}", employee.getId(), employee.getName());
        return employee;
    }

    @Override
    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees");
        return employeeMapper.findAll();
    }

    @Transactional
    @Override
    public void createEmployee(Employee employee) {
        try {
            employeeMapper.save(employee);
            log.info("Employee created: id={}, name={}", employee.getId(), employee.getName());
        } catch (DuplicateKeyException e) {
            throw new ConflictException("Employee with ID " + employee.getId() + " already exists.");
        }
    }

    @Transactional
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

    @Transactional
    @Override
    public void deleteEmployee(Long id) {
        checkEmployeeExists(id);

        employeeMapper.deleteById(id);
        log.info("Employee deleted: id={}", id);
    }

    @Override
    public void checkEmployeeExists(Long id) {
        if (!employeeMapper.existsById(id)) {
            log.warn("Employee not found with id: {}", id);
            throw new NotFoundException();
        }
    }
}
