package com.pofa.ebcadmin.order.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.pofa.ebcadmin.order.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Collection;

@Mapper
public interface OrderDao extends BaseMapper<OrderInfo> {
    Integer replaceBatchSomeColumn(Collection<OrderInfo> entityList);
}
