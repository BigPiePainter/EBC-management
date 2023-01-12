package com.pofa.ebcadmin.user.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pofa.ebcadmin.category.entity.CategoryInfo;
import com.pofa.ebcadmin.department.dao.DepartmentDao;
import com.pofa.ebcadmin.team.dao.TeamDao;
import com.pofa.ebcadmin.user.dao.UserDao;
import com.pofa.ebcadmin.user.dto.SysUser;
import com.pofa.ebcadmin.user.entity.UserInfo;
import com.pofa.ebcadmin.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    public UserDao userDao;

    @Autowired
    public UserInfo userInfo;


    @Autowired
    public DepartmentDao departmentDao;

    @Autowired
    public TeamDao teamDao;


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, timeout = 30 * 1000)
    public int userRegistry(SysUser.RegistDTO dto) {

        if (dto.getUsername().length() > 50) {
            return -101;
        }

        if (dto.getPassword().length() > 50) {
            return -102;
        }

        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("username", dto.getUsername());
        List<UserInfo> userInfos = userDao.selectList(wrapper);
        log.info(String.valueOf(dto));
        if (userInfos.isEmpty()) {
            return userDao.insert(userInfo
                    .setDepartment(dto.getDepartment())
                    .setOnboardingTime(dto.getOnboardingTime())
                    .setGender(dto.getGender())
                    .setContact(dto.getContact())
                    .setPermission(dto.getPermission())
                    .setUsername(dto.getUsername())
                    .setPassword(dto.getPassword())
                    .setNick(dto.getNick())
                    .setNote(dto.getNote())
            );
        }
        return -100;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int editUser(SysUser.EditDTO dto) {
        if (!userDao.selectList(new QueryWrapper<UserInfo>().ne("uid", dto.getUid()).eq("username", dto.getUsername())).isEmpty()) {
            return -100;
        }

        return userDao.update(userInfo
                        .setDepartment(dto.getDepartment())
                        .setOnboardingTime(dto.getOnboardingTime())
                        .setGender(dto.getGender())
                        .setContact(dto.getContact())
                        .setPermission(dto.getPermission())
                        .setUsername(dto.getUsername())
                        .setPassword(dto.getPassword())
                        .setNick(dto.getNick())
                        .setNote(dto.getNote()),
                new UpdateWrapper<UserInfo>().eq("uid", dto.getUid()));
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
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int changePassword(Long uid, String password) {
        return userDao.update(null, new UpdateWrapper<UserInfo>().eq("uid", uid).set("password", password));
    }


//    @Override
//    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
//    public JSONObject getUserRelationsWithinAuthorityById(Long id) {
//
//        // 张清宇 2022-07-19
//        // 如果有一天需要优化下面的数据库树状结构递归查询，优化方案如下
//        // https://www.sitepoint.com/hierarchical-data-database-2/
//
//        var subUser = new JSONObject();
//        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
//        wrapper.eq("creator_id", id).select("uid");
//        var users = userDao.selectList(wrapper);
//        for (UserInfo user : users) {
//            subUser.put(user.getUid().toString(), _getUserRelationsWithinAuthorityById(user.getUid()));
//        }
//        return new JSONObject().fluentPut(id.toString(), subUser);
//    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<UserInfo> getUsersWithinAuthorityByUser(UserInfo user) {


        var wrapper = new QueryWrapper<UserInfo>();


        if (user.getUid() != 1L) {

            var permission = JSON.parseObject(user.getPermission());
            System.out.println(permission);
            var departmentIds = permission.getJSONObject("c").getJSONArray("d").stream().map(i -> Long.valueOf((Integer) i)).toList();
            System.out.println(departmentIds);
            if (!departmentIds.isEmpty()) {
                wrapper.in("department", departmentIds);
            }

        }

        var users = userDao.selectList(wrapper);

        return users;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<UserInfo> getUserInfosByIds(List<Long> users) {
        return userDao.selectList(new QueryWrapper<UserInfo>().in("uid", users));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<UserInfo> getAllUserSimplifyInfos() {
        return userDao.selectList(new QueryWrapper<UserInfo>().select("uid", "username", "nick", "contact", "permission", "note"));
    }

//    private JSONObject _getUserRelationsWithinAuthorityById(Long id) {
//        var subUser = new JSONObject();
//        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
//        wrapper.eq("creator_id", id).select("uid");
//        var users = userDao.selectList(wrapper);
//        for (UserInfo user : users) {
//            subUser.put(user.getUid().toString(), _getUserRelationsWithinAuthorityById(user.getUid()));
//        }
//        return subUser;
//    }
//
//    public List<Long> _getUsersWithinAuthorityById(Long id) {
//        var subUser = new ArrayList<Long>();
//        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
//        wrapper.eq("creator_id", id).select("uid");
//        var users = userDao.selectList(wrapper);
//        for (UserInfo user : users) {
//            subUser.add(user.getUid());
//            subUser.addAll(_getUsersWithinAuthorityById(user.getUid()));
//        }
//        return subUser;
//    }

}
