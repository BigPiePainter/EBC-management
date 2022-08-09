package com.pofa.ebcadmin.team.controller;


import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.department.dto.Department;
import com.pofa.ebcadmin.team.dto.Team;
import com.pofa.ebcadmin.team.service.TeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "组别")
@Controller
@RestController
@RequestMapping("team")
public class TeamController {
    @Autowired
    public TeamService teamService;


    @ApiOperation(value = "添加组别", notes = "",
            httpMethod = "POST")
    @PostMapping("/add")
    public SaResult addTeam(Team.AddDTO dto) {
        System.out.println("addTeam TEST");
        System.out.println(dto);
        var code = teamService.addTeam(dto);

        String data = switch (code) {
            case 1 -> "添加成功";
            case -100 -> "组别已存在";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "修改组别信息", notes = "",
            httpMethod = "POST")
    @PostMapping("/modify")
    public SaResult editManufacturer(Team.EditDTO dto) {
        var code = teamService.editTeam(dto);

        String data = switch (code) {
            case 1 -> "修改成功";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "获取所有组别信息", notes = "", httpMethod = "POST")
    @PostMapping("/get")
    public SaResult getTeams(Team.GetDTO dto) {
        var teams = teamService.getTeams();
        return SaResult.ok("success").setData(new JSONObject().fluentPut("teams", teams));
    }
}
