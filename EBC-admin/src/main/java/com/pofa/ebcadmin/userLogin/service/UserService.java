package com.pofa.ebcadmin.userLogin.service;

import com.pofa.ebcadmin.userLogin.entity.UserInfo;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {

    int userRegistry(String username, String password);

    List<UserInfo> userLogin(String username, String password);
}
