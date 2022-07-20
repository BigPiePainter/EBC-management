package com.pofa.ebcadmin.userLogin.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pofa.ebcadmin.userLogin.dao.DepartmentDao;
import com.pofa.ebcadmin.userLogin.dao.ProductDao;
import com.pofa.ebcadmin.userLogin.dto.Department;
import com.pofa.ebcadmin.userLogin.dto.Product;
import com.pofa.ebcadmin.userLogin.entity.DepartmentInfo;
import com.pofa.ebcadmin.userLogin.entity.ProductInfo;
import com.pofa.ebcadmin.userLogin.entity.UserInfo;
import com.pofa.ebcadmin.userLogin.service.DepartmentService;
import com.pofa.ebcadmin.userLogin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    public DepartmentDao departmentDao;

    @Autowired
    public DepartmentInfo departmentInfo;


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int departmentAdd(Department.AddDTO dto) {
        QueryWrapper<DepartmentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("name", dto.getName());
        List<DepartmentInfo> departmentInfos = departmentDao.selectList(wrapper);
        if (departmentInfos.isEmpty()) {
            return departmentDao.insert(departmentInfo.setName(dto.getName()).setNote(dto.getNote()));
        }
        return -100;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public List<DepartmentInfo> getAllDepartments() {
        return departmentDao.selectList(null);
    }

}
