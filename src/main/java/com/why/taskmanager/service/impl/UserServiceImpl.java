package com.why.taskmanager.service.impl;


import com.why.taskmanager.entity.Users;
import com.why.taskmanager.mapper.UserMapper;
import com.why.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Users findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public void save(Users user) {
        userMapper.insert(user);
    }

    @Override
    public void deleteUser(Long userId) {
        Users user = userMapper.selectById(userId);
        if (user != null) {
            user.setIsDeleted(true);
            userMapper.updateById(user);
        }
    }

    @Override
    public void updateUser(Users user) {
        userMapper.updateById(user);
    }
}
