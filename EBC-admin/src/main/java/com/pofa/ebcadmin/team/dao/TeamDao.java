package com.pofa.ebcadmin.team.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pofa.ebcadmin.team.entity.TeamInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeamDao extends BaseMapper<TeamInfo> {

}
