package com.pofa.ebcadmin.category.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pofa.ebcadmin.category.entity.CategoryHistoryInfo;
import com.pofa.ebcadmin.category.entity.CategoryInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryHistoryDao extends BaseMapper<CategoryHistoryInfo> {

}
