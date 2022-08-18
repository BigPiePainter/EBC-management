package com.pofa.ebcadmin.product.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.manufacturer.dto.Manufacturer;
import com.pofa.ebcadmin.product.dto.Product;
import com.pofa.ebcadmin.product.dto.Sku;
import com.pofa.ebcadmin.product.service.ProductService;
import com.pofa.ebcadmin.userLogin.entity.UserInfo;
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

import java.math.BigDecimal;

@Api(tags = "商品信息")
@Controller
@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    public UserService userService;
    @Autowired
    public ProductService productService;

    @ApiOperation(value = "新增商品", notes = "新增产品信息", httpMethod = "POST")
    @PostMapping("/add")
    public SaResult productAdd(Product.AddDTO dto) {
        System.out.println("ADD TEST");
        int code = productService.addProduct(dto);

        String data = switch (code) {
            case 1 -> "创建成功";
            case -100 -> "当前商品ID已存在";
            default -> "未知错误";
        };

        //return new JsonResponse(code, data);
        return SaResult.ok("success").setData(data).setCode(code);
    }


    @ApiOperation(value = "修改商品信息", notes = "根据商品ID",
            httpMethod = "POST")
    @PostMapping("/modify")
    public SaResult editProduct(Product.EditDTO dto) {

        System.out.println("Product EDIT TEST");
        System.out.println(dto);
        var code = productService.editProduct(dto);

        String data = switch (code) {
            case 1 -> "修改成功";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }



    @ApiOperation(value = "获取权限内的商品列表", notes = "需要登陆, 无法直接测试", httpMethod = "POST")
    @ApiImplicitParams({
   })
    @SaCheckLogin
    @PostMapping("/getByPermission")
    public SaResult productGet(Product.GetDTO dto) {
        System.out.println("GET TEST");
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


    @ApiOperation(value = "获取权限内的事业部, 组别, 商店名等类别", notes = "需要登陆", httpMethod = "POST")
    @ApiImplicitParams({
    })
    @SaCheckLogin
    @PostMapping("/getCategory")
    public SaResult getCategory() {
        System.out.println("getCategory TEST");

        //查询所有下级User
        System.out.println(StpUtil.getLoginIdAsLong());
        var users = userService.getUserIdsWithinAuthorityById(StpUtil.getLoginIdAsLong());
        System.out.println(users);

        JSONObject data = productService.getCategorysByUserIds(users);

        return SaResult.ok("success").setData(data);
    }

    @ApiOperation(value = "删除商品", notes = "将商品挪入回收站", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uids", value = "要删除的商品的UID，用英文逗号隔开", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "1000,1001", required = false),
    })
    @PostMapping("/delete")
    public SaResult deleteProduct(Product.deleteDTO dto) {
        System.out.println("deleteProduct TEST");

        var code = productService.deprecateProductByUid(dto.getUid());

        System.out.println(code);

        String data;
        if (code > 0) {
            data = "成功删除" + code + "条商品";
        } else {
            data = switch (code) {
                default -> "未知错误";
            };
        }


        return SaResult.ok("success").setData(data).setCode(code);
    }

//    @ApiOperation(value = "删除商品", notes = "需要登陆", httpMethod = "POST")
//    @ApiImplicitParams({
//    })
//    @SaCheckLogin
//    @PostMapping("/deleteProduct")
//    public SaResult deleteProduct() {
//        System.out.println("deleteProduct TEST");
//
//        //查询所有下级User
//        System.out.println(StpUtil.getLoginIdAsLong());
//        var users = userService.getUserIdsWithinAuthorityById(StpUtil.getLoginIdAsLong());
//        System.out.println(users);
//
//        JSONObject data = productService.getCategorysByUserIds(users);
//
//        return SaResult.ok("success").setData(data);
//    }
}
