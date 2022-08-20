package com.pofa.ebcadmin.order.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.pofa.ebcadmin.order.entity.FakeOrderInfo;
import com.pofa.ebcadmin.order.entity.OrderInfo;
import com.pofa.ebcadmin.product.entity.ProductInfo;
import com.pofa.ebcadmin.product.entity.SkuInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface FakeOrderDao extends BaseMapper<FakeOrderInfo> {

    Integer replaceBatchSomeColumn(Collection<FakeOrderInfo> entityList);
}
