package com.pofa.ebcadmin.team.controller;


import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "组别名称", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "554", required = false),
            @ApiImplicitParam(name = "note", value = "注释", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
    })
    @PostMapping("/add")
    public SaResult departmentAdd(Team.AddDTO dto) {

        var code = teamService.addTeam(dto);

        String data = switch (code) {
            case 1 -> "添加成功";
            case -100 -> "组别已存在";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "获取所有组别信息", notes = "", httpMethod = "POST")
    @ApiImplicitParams({})
    @PostMapping("/get")
    public SaResult departmentGet(Team.GetDTO dto) {
        var departments = teamService.getTeams();
        return SaResult.ok("success").setData(new JSONObject().fluentPut("teams", departments));
    }
}
