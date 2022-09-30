package com.pofa.ebcadmin.userLogin.service;

import com.pofa.ebcadmin.userLogin.dto.SysUser;
import com.pofa.ebcadmin.userLogin.entity.UserInfo;

import java.util.List;

public interface UserService {

    int userRegistry(SysUser.RegistDTO dto);

    int editUser(SysUser.EditDTO dto);

    List<UserInfo> userLogin(String username, String password);

//    JSONObject getUserRelationsWithinAuthorityById(Long id);
//
    List<UserInfo> getUsersWithinAuthorityByUser(UserInfo user);

    List<UserInfo> getUserInfosByIds(List<Long> users);

    List<UserInfo> getAllUserSimplifyInfos();

}
