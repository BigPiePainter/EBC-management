package com.pofa.ebcadmin.team.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pofa.ebcadmin.department.dto.Department;
import com.pofa.ebcadmin.department.entity.DepartmentInfo;
import com.pofa.ebcadmin.team.dao.TeamDao;
import com.pofa.ebcadmin.team.dto.Team;
import com.pofa.ebcadmin.team.entity.TeamInfo;
import com.pofa.ebcadmin.team.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    public TeamDao teamDao;

    @Autowired
    public TeamInfo teamInfo;


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int addTeam(Team.AddDTO dto) {
        var teamInfos = teamDao.selectList(new QueryWrapper<TeamInfo>().eq("name", dto.getName()));
        if (teamInfos.isEmpty()) {
            return teamDao.insert(teamInfo
                    .setName(dto.getName())
                    .setAdmin(dto.getAdmin())
                    .setNote(dto.getNote()));
        }
        return -100;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int editTeam(Team.EditDTO dto) {
        return teamDao.update(teamInfo
                        .setName(dto.getName())
                        .setAdmin(dto.getAdmin())
                        .setNote(dto.getNote()),
                new UpdateWrapper<TeamInfo>().eq("uid", dto.getUid()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<TeamInfo> getTeams() {
        return teamDao.selectList(null);
    }
}
