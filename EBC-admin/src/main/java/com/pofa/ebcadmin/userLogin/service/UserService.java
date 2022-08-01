package com.pofa.ebcadmin.userLogin.service;

import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.userLogin.dto.SysUser;
import com.pofa.ebcadmin.userLogin.entity.UserInfo;

import java.util.List;

public interface UserService {

    int userRegistry(SysUser.RegistDTO dto);

    List<UserInfo> userLogin(String username, String password);

    JSONObject getUserRelationsWithinAuthorityById(Long id);

    List<Long> getUserIdsWithinAuthorityById(Long id);

    List<UserInfo> getUserInfosByIds(List<Long> users);
}
