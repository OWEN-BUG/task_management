package com.why.taskmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.why.taskmanager.entity.Users;
import com.why.taskmanager.mapper.UserMapper;
import com.why.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<Users> findAllUsers() {
        return userMapper.selectList(new QueryWrapper<Users>().eq("is_deleted", false));
    }

    @Override
    public List<Users> findNormalUsers() {
        return userMapper.selectList(
                new QueryWrapper<Users>()
                        .eq("role", 9)
                        .eq("is_deleted", false)
        );
    }
}