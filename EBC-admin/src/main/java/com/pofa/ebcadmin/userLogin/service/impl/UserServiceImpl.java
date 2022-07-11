package com.pofa.ebcadmin.userLogin.service.impl;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pofa.ebcadmin.userLogin.dao.UserDao;
import com.pofa.ebcadmin.userLogin.entity.UserInfo;
import com.pofa.ebcadmin.userLogin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    public UserDao userDao;

    @Autowired
    public UserInfo userInfo;

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, timeout = 30 * 1000)
    @Async
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
        if (userInfos.isEmpty()){
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
}
