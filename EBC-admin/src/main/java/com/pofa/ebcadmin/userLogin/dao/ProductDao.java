package com.pofa.ebcadmin.userLogin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pofa.ebcadmin.userLogin.entity.ProductInfo;
import com.pofa.ebcadmin.userLogin.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductDao extends BaseMapper<ProductInfo> {

}
