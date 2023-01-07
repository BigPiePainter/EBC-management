package com.pofa.ebcadmin.manufacturer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pofa.ebcadmin.manufacturer.entity.ManufacturerInfo;
import com.pofa.ebcadmin.product.entity.SkuInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface ManufacturerDao extends BaseMapper<ManufacturerInfo> {
    Integer insertBatchSomeColumn(Collection<ManufacturerInfo> entityList);
}
