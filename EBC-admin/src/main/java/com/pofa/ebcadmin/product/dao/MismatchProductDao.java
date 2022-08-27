package com.pofa.ebcadmin.product.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.pofa.ebcadmin.order.entity.OrderInfo;
import com.pofa.ebcadmin.product.entity.MismatchProductInfo;
import com.pofa.ebcadmin.product.entity.ProductInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface MismatchProductDao extends BaseMapper<MismatchProductInfo> {
    Integer replaceBatchSomeColumn(Collection<MismatchProductInfo> entityList);
}
