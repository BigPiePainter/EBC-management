package com.pofa.ebcadmin.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pofa.ebcadmin.order.entity.FakeOrderInfo;
import com.pofa.ebcadmin.order.entity.PersonalFakeOrderInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface PersonalFakeOrderDao extends BaseMapper<PersonalFakeOrderInfo> {

    Integer replaceBatchSomeColumn(Collection<PersonalFakeOrderInfo> entityList);
}
