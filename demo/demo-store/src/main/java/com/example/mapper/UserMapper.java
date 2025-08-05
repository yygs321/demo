package com.example.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findById(Long id);
    List<User> findAll();
    void save(User user);
    void update(User user);
    void deleteById(Long id);
    boolean existsById(Long id);
}
