package com.pofa.ebcadmin.department.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pofa.ebcadmin.department.dao.DepartmentDao;
import com.pofa.ebcadmin.department.dto.Department;
import com.pofa.ebcadmin.department.entity.DepartmentInfo;
import com.pofa.ebcadmin.department.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    public DepartmentDao departmentDao;

    @Autowired
    public DepartmentInfo departmentInfo;


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int addDepartment(Department.AddDTO dto) {
        var departmentInfos = departmentDao.selectList(new QueryWrapper<DepartmentInfo>().eq("name", dto.getName()));
        if (departmentInfos.isEmpty()) {
            return departmentDao.insert(departmentInfo
                    .setName(dto.getName())
                    .setAdmin(dto.getAdmin())
                    .setNote(dto.getNote()));
        }
        return -100;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int editDepartment(Department.EditDTO dto) {
        return departmentDao.update(departmentInfo
                        .setName(dto.getName())
                        .setAdmin(dto.getAdmin())
                        .setNote(dto.getNote()),
                new UpdateWrapper<DepartmentInfo>().eq("uid", dto.getUid()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<DepartmentInfo> getDepartments() {
        return departmentDao.selectList(null);
    }
}
