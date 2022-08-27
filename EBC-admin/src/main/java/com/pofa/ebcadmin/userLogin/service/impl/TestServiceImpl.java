package com.pofa.ebcadmin.userLogin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pofa.ebcadmin.userLogin.dao.TestDao;
import com.pofa.ebcadmin.userLogin.dao.UserDao;
import com.pofa.ebcadmin.userLogin.entity.UserInfo;
import com.pofa.ebcadmin.userLogin.service.TestService;
import com.pofa.ebcadmin.userLogin.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TestServiceImpl implements TestService {

    @Autowired
    public TestDao testDao;

    @Autowired
    public UserInfo userInfo;


    @Override
//    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
//    @Async
    public void addDatas(int num) {
        var time = new Date().getTime();
        log.info(String.valueOf(time));
        List<UserInfo> array = new ArrayList<UserInfo>();
        for (int a = 0; a < num; a++) {
            array.add(userInfo.setUsername("556").setPassword("123321"));
        }

        log.info(String.valueOf(new Date().getTime() - time));
    }
}
