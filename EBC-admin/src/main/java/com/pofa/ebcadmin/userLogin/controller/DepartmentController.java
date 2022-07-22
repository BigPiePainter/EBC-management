package com.pofa.ebcadmin.userLogin.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.userLogin.dto.Product;
import com.pofa.ebcadmin.userLogin.entity.UserInfo;
import com.pofa.ebcadmin.userLogin.service.ProductService;
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

@Api(tags = "部门信息")
@Controller
@RestController
@RequestMapping("department")
public class DepartmentController {

    @Autowired
    public UserService userService;
    @Autowired
    public ProductService productService;

    @ApiOperation(value = "新增部门", notes = "新增产品信息", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "部门名称", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "554", required = false),
            @ApiImplicitParam(name = "note", value = "备注", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
 })
    @PostMapping("/add")
    public SaResult productAdd(Product.AddDTO dto) {
        System.out.println("收到了请求");
        int code = productService.addProduct(dto);

        String data = switch (code) {
            case 1 -> "创建成功";
            case -100 -> "当前商品ID已存在";
            default -> "未知错误";
        };

        //return new JsonResponse(code, data);
        return SaResult.ok("success").setData(data).setCode(code);
    }



    @ApiOperation(value = "获取权限内的商品列表", notes = "需要登陆, 无法直接测试", httpMethod = "POST")
    @ApiImplicitParams({
   })
    @SaCheckLogin
    @PostMapping("/getByPermission")
    public SaResult productGet(Product.GetDTO dto) {
        System.out.println("收到获取");
        if (!StpUtil.isLogin()){
            return SaResult.ok("success").setData("用户未登录");
        }
        UserInfo user = (UserInfo) StpUtil.getSession().get("user");
        System.out.println(user);
        System.out.println(dto);

        //查询所有下级User
        System.out.println(StpUtil.getLoginIdAsLong());
        var users = userService.getUserIdsWithinAuthorityById(StpUtil.getLoginIdAsLong());
        System.out.println(users);

        JSONObject data = productService.getProductsByUserIds(users, dto);

        return SaResult.ok("success").setData(data);
    }


    @ApiOperation(value = "获取权限内的事业部, 组别, 商店名等类别", notes = "需要登陆, 无法直接测试", httpMethod = "POST")
    @ApiImplicitParams({
    })
    @SaCheckLogin
    @PostMapping("/getCategory")
    public SaResult getCategory() {
        System.out.println("收到获取");
        //if (!StpUtil.isLogin()){
        //    return SaResult.ok("success").setData("用户未登录");
        //}
        //UserInfo user = (UserInfo) StpUtil.getSession().get("user");
        //System.out.println(user);

        //查询所有下级User
        System.out.println(StpUtil.getLoginIdAsLong());
        var users = userService.getUserIdsWithinAuthorityById(StpUtil.getLoginIdAsLong());
        System.out.println(users);

        JSONObject data = productService.getCategorysByUserIds(users);

        return SaResult.ok("success").setData(data);
    }
}
