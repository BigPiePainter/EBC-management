package com.pofa.ebcadmin.team.service;

import com.pofa.ebcadmin.team.dto.Team;
import com.pofa.ebcadmin.team.entity.TeamInfo;

import java.util.List;

public interface TeamService {

    int addTeam(Team.AddDTO dto);

    List<TeamInfo> getTeams();

}
