package com.newcoder.service.impl;

import com.newcoder.mapper.UserMapper;
import com.newcoder.pojo.User;
import com.newcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }
}
