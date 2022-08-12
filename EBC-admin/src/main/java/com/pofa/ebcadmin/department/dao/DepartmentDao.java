package com.pofa.ebcadmin.department.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pofa.ebcadmin.department.entity.DepartmentInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DepartmentDao extends BaseMapper<DepartmentInfo> {

}
