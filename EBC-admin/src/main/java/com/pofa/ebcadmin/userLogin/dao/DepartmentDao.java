package com.pofa.ebcadmin.userLogin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pofa.ebcadmin.userLogin.entity.DepartmentInfo;
import com.pofa.ebcadmin.userLogin.entity.ProductInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DepartmentDao extends BaseMapper<DepartmentInfo> {

}
