package com.example.service;

import com.example.entity.User;
import com.example.exception.UserNotFoundException;
import com.example.mapper.UserMapper;
import com.example.spec.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public User getUserById(Long id) {
        User user = userMapper.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        log.info("User found: id={}, name={}", user.getId(), user.getName());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    @Override
    public void createUser(User user) {
        userMapper.save(user);
        log.info("User created: id={}, name={}", user.getId(), user.getName());
    }

    @Override
    public void updateUser(Long id, User user) {
        getUserById(id);

        User userToUpdate = User.builder()
                .id(id)
                .name(user.getName())
                .build();

        userMapper.update(userToUpdate);
        log.info("User updated: id={}, new name={}", userToUpdate.getId(), userToUpdate.getName());
    }

    @Override
    public void deleteUser(Long id) {
        checkUserExists(id);

        userMapper.deleteById(id);
        log.info("User deleted: id={}", id);
    }

    @Override
    public void checkUserExists(Long id) {
        if (!userMapper.existsById(id)) {
            throw new UserNotFoundException(id);
        }
    }
}
