package com.pofa.ebcadmin.userLogin.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.userLogin.dto.Sku;
import com.pofa.ebcadmin.userLogin.service.ProductService;
import com.pofa.ebcadmin.userLogin.service.SkuService;
import com.pofa.ebcadmin.userLogin.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Api(tags = "SKU")
@Controller
@RestController
@RequestMapping("sku")
public class SkuInfoController {


    @Autowired
    public UserService userService;
    @Autowired
    public ProductService productService;

    @Autowired
    public SkuService skuService;


    @ApiOperation(value = "批量添加SKU", notes = "根据商品ID", httpMethod = "POST")
    @ApiImplicitParams({
    })
    @PostMapping("/add")
    public SaResult addSkus(@RequestBody JSONObject json) {
        System.out.println(json);

        System.out.println("addSkus TEST");
        var code = skuService.addSkus(json.getJSONArray("data"));


        return SaResult.ok("success").setData("。。。！？");
    }

    @ApiOperation(value = "读取SKU", notes = "根据商品ID", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "商品ID", dataType = "Long", paramType = "query", dataTypeClass = Long.class, example = "1000000", required = false),
    })
    @PostMapping("/get")
    public SaResult getSkus(Sku.getDTO dto) {
        System.out.println("getSkus TEST");
        System.out.println(new Date().getTime());
        var skus = skuService.getSkusByProductId(dto.getProductId());
        System.out.println(new Date().getTime());
        return SaResult.ok("success").setData(new JSONObject().fluentPut("skus", skus));
    }

    @ApiOperation(value = "删除SKU", notes = "将SKU挪如回收站", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "SKU唯一UID", dataType = "Long", paramType = "query", dataTypeClass = Long.class, example = "1000000", required = false),
    })
    @PostMapping("/delete")
    public SaResult deleteSku(Sku.deleteDTO dto) {
        System.out.println("deleteSku TEST");
        System.out.println(new Date().getTime());


        var code = skuService.deprecateSkuByUid(dto.getUid());
        System.out.println(new Date().getTime());


        String data = switch (code) {
            case 1 -> "删除成功";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }
}
