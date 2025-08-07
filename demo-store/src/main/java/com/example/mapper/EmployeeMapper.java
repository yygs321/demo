package com.example.mapper;

import com.example.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EmployeeMapper {
    Optional<Employee> findById(Long id);
    List<Employee> findAll();
    void save(Employee employee);
    void update(Employee employee);
    void deleteById(Long id);
    boolean existsById(Long id);
    void saveAll(List<Employee> employees);
}