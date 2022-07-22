package com.pofa.ebcadmin.userLogin.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.mysql.cj.xdevapi.JsonArray;
import com.nimbusds.jose.JOSEException;
import com.pofa.ebcadmin.userLogin.dto.SysUser;
import com.pofa.ebcadmin.userLogin.entity.UserInfo;
import com.pofa.ebcadmin.userLogin.service.TestService;
import com.pofa.ebcadmin.userLogin.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@Api(tags = "用户请求")
@Controller
@RestController
@RequestMapping("user")
public class UserLoginController {
    @Autowired
    public UserService userService;

    @Autowired
    public TestService testService;


    @ApiOperation(value = "登录", notes = "用于用户的登录接口,要么账号或者手机号和密码登录，要么手机和验证码登录，两个选择必须选一个！",
            httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "登录账号", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "554", required = false),
            @ApiImplicitParam(name = "password", value = "登录密码", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
    })
    @PostMapping("/login")
    public SaResult userLogin(SysUser.LoginDTO user) throws ParseException, JOSEException {
        List<UserInfo> userInfos = userService.userLogin(user.getUsername(), user.getPassword());
        if (!userInfos.isEmpty()) {
            StpUtil.login(userInfos.get(0).getUid());
            StpUtil.getSession().set("user", userInfos.get(0));
            return SaResult.ok("success").setData(StpUtil.getTokenInfo());
        }
        return SaResult.ok("success").setData("账号或密码错误");
    }

    @ApiOperation(value = "注册", notes = "用于用户的注册，账号+密码",
            httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "注册账号", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "554", required = false),
            @ApiImplicitParam(name = "password", value = "注册密码", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
    })
    @PostMapping("/regist")
    public SaResult userRegist(SysUser.LoginDTO user) {
        int code = userService.userRegistry(user.getUsername(), user.getPassword());

        String data = switch (code) {
            case 1 -> "创建成功";
            case -100 -> "账号已存在";
            case -101 -> "账号格式错误";
            case -102 -> "密码格式错误";
            default -> "未知错误";
        };


        //return new JsonResponse(code, data);
        return SaResult.ok("success").setData(data).setCode(code);
    }


    @ApiOperation(value = "获取所有下级从属关系", notes = "需要登陆，无法测试",
            httpMethod = "POST")
    @ApiImplicitParams({
    })
    @SaCheckLogin
    @PostMapping("/getSubUserRelations")
    public SaResult getSubUserRelations() {
        var result = userService.getUserRelationsWithinAuthorityById(StpUtil.getLoginIdAsLong());
        return SaResult.ok("success").setData(result);
    }


    @ApiOperation(value = "获取所有下级账号信息", notes = "需要登陆，无法测试",
            httpMethod = "POST")
    @ApiImplicitParams({
    })
    @SaCheckLogin
    @PostMapping("/getSubUsers")
    public SaResult getSubUsers() {
        var users = userService.getUserIdsWithinAuthorityById(StpUtil.getLoginIdAsLong());
        var userInfos = userService.getUserInfosByIds(users);
        return SaResult.ok("success").setData(new JSONObject().fluentPut("userInfos", userInfos));
    }


//    @ApiOperation(value = "获取所有下级账号信息", notes = "测试专用",
//            httpMethod = "POST")
//    @ApiImplicitParams({
//    })
//    @PostMapping("/test")
//    public SaResult test() {
//        var users = userService.getUserIdsWithinAuthorityById(0L);
//        var userInfos = userService.getUserInfosByIds(users);
//        return SaResult.ok("success").setData(new JSONObject().fluentPut("userInfos", userInfos));
//    }


    /*
    public JsonResult userTokenTest(String token) throws JOSEException, ParseException {
        System.out.println(token);
        JsonResult success = new JsonResult(this.token.volidToken(token));
        return success;
    }

     */
}