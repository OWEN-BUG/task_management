package com.why.taskmanager.service;


import com.why.taskmanager.entity.Users;

public interface UserService {
    Users findByUsername(String username);
    void save(Users user);
    void deleteUser(Long userId);
    void updateUser(Users user);
}
