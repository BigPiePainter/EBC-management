package com.pofa.ebcadmin.user.service;

import com.pofa.ebcadmin.user.dto.SysUser;
import com.pofa.ebcadmin.user.entity.UserInfo;

import java.util.List;

public interface UserService {

    int userRegistry(SysUser.RegistDTO dto);

    int editUser(SysUser.EditDTO dto);


    int changePassword(Long uid, String password);

    List<UserInfo> userLogin(String username, String password);

//    JSONObject getUserRelationsWithinAuthorityById(Long id);
//
    List<UserInfo> getUsersWithinAuthorityByUser(UserInfo user);

    List<UserInfo> getUserInfosByIds(List<Long> users);

    List<UserInfo> getAllUserSimplifyInfos();

}
