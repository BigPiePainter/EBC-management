package com.pofa.ebcadmin.user.service.impl;

import com.pofa.ebcadmin.user.dao.TestDao;
import com.pofa.ebcadmin.user.entity.UserInfo;
import com.pofa.ebcadmin.user.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
