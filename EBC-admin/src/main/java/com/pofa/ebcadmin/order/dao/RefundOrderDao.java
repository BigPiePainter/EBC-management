package com.pofa.ebcadmin.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pofa.ebcadmin.order.entity.FakeOrderInfo;
import com.pofa.ebcadmin.order.entity.RefundOrderInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface RefundOrderDao extends BaseMapper<RefundOrderInfo> {

    Integer replaceBatchSomeColumn(Collection<RefundOrderInfo> entityList);
}
