package com.pofa.ebcadmin.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pofa.ebcadmin.user.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestDao extends BaseMapper<UserInfo> {

}