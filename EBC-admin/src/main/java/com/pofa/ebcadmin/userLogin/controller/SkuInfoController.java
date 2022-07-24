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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public SaResult addSkus(Sku.addDTO dto) {
        System.out.println("addSkus TEST");
        System.out.println(dto.getProductId());
        //System.out.println(dto.getData());

        var code = skuService.addSkusByProductId(dto.getProductId(), JSON.parseArray(dto.getData()));

        return SaResult.ok("success").setData("。。。！？");
    }

    @ApiOperation(value = "读取SKU", notes = "根据商品ID", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "商品ID", dataType = "Long", paramType = "query", dataTypeClass = Long.class, example = "1000000", required = false),
    })
    @PostMapping("/get")
    public SaResult getSkus(Sku.getDTO dto) {
        System.out.println("getSkus TEST");
        var skus = skuService.getSkusByProductId(dto.getProductId());
        return SaResult.ok("success").setData(new JSONObject().fluentPut("skus", skus));
    }
}
