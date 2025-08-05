package com.example.spec;

import com.example.entity.User;

import java.util.List;

public interface UserService {
    User getUserById(Long id);
    List<User> getAllUsers();
    void createUser(User user);
    void updateUser(Long id, User user);
    void deleteUser(Long id);
    void checkUserExists(Long id);
}
