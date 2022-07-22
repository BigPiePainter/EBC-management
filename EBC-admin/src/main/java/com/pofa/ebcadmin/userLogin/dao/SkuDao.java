package com.pofa.ebcadmin.userLogin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pofa.ebcadmin.userLogin.entity.SkuInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface SkuDao extends BaseMapper<SkuInfo> {
    Integer insertBatchSomeColumn(Collection<SkuInfo> entityList);
}
