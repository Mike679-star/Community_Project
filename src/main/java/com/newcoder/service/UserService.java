package com.newcoder.service;

import com.newcoder.pojo.LoginTicket;
import com.newcoder.pojo.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public interface UserService {

    User findUserById(int id);

    Map<String, Object> register(User user);

    int activation(int userId, String code);

    Map<String, Object> login(String username, String password, int expiredSeconds);

    void logout(String ticket);

    LoginTicket findLoginTicket(String ticket);

    int updateHeader(int userId, String headerUrl);

    int updatePassword(int id, String newPassword);

    User findUserByName(String username);

    Collection<? extends GrantedAuthority> getAuthorities(int userId);
}
