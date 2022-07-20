package com.pofa.ebcadmin.userLogin.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pofa.ebcadmin.userLogin.dao.UserDao;
import com.pofa.ebcadmin.userLogin.entity.UserInfo;
import com.pofa.ebcadmin.userLogin.service.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    public UserDao userDao;

    @Autowired
    public UserInfo userInfo;

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, timeout = 30 * 1000)
    public int userRegistry(String username, String password) {
        if (username.length() > 50) {
            return -101;
        }

        if (password.length() > 50) {
            return -102;
        }

        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        List<UserInfo> userInfos = userDao.selectList(wrapper);
        if (userInfos.isEmpty()) {
            return userDao.insert(userInfo.setUsername(username).setPassword(password));
        }
        return -100;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<UserInfo> userLogin(String username, String password) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username)
                .eq("password", password);
        return userDao.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public JSONObject getUserRelationsWithinAuthorityById(Long id) {

        // 张清宇 2022-07-19
        // 如果有一天需要优化下面的数据库树状结构递归查询，优化方案如下
        // https://www.sitepoint.com/hierarchical-data-database-2/

        var subUser = new JSONObject();
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("creator_id", id).select("uid");
        var users = userDao.selectList(wrapper);
        for (UserInfo user : users) {
            subUser.put(user.getUid().toString(), _getUserRelationsWithinAuthorityById(user.getUid()));
        }
        return new JSONObject().fluentPut(id.toString(), subUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<Long> getUserIdsWithinAuthorityById(Long id) {
        var users = _getUsersWithinAuthorityById(id);
        users.add(id);
        return users;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<UserInfo> getUserInfosByIds(List<Long> users) {
        return userDao.selectList(new QueryWrapper<UserInfo>().in("uid", users));
    }

    private JSONObject _getUserRelationsWithinAuthorityById(Long id) {
        var subUser = new JSONObject();
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("creator_id", id).select("uid");
        var users = userDao.selectList(wrapper);
        for (UserInfo user : users) {
            subUser.put(user.getUid().toString(), _getUserRelationsWithinAuthorityById(user.getUid()));
        }
        return subUser;
    }

    public List<Long> _getUsersWithinAuthorityById(Long id) {
        var subUser = new ArrayList<Long>();
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("creator_id", id).select("uid");
        var users = userDao.selectList(wrapper);
        for (UserInfo user : users) {
            subUser.add(user.getUid());
            subUser.addAll(_getUsersWithinAuthorityById(user.getUid()));
        }
        return subUser;
    }

}
