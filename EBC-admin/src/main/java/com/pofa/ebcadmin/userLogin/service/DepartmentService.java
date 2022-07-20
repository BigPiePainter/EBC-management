package com.pofa.ebcadmin.userLogin.service;

import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.userLogin.dto.Department;
import com.pofa.ebcadmin.userLogin.dto.Product;
import com.pofa.ebcadmin.userLogin.entity.DepartmentInfo;

import java.util.List;

public interface DepartmentService {
    int departmentAdd(Department.AddDTO dto);

    List<DepartmentInfo> getAllDepartments();
}
