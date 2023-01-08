package com.pofa.ebcadmin.shop.controller;


import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.shop.dto.Shop;
import com.pofa.ebcadmin.shop.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "店铺")
@Controller
@RestController
@RequestMapping("shop")
@Slf4j
public class ShopController {
    @Autowired
    public ShopService shopService;

    @ApiOperation(value = "获取店铺", notes = "",
            httpMethod = "POST")
    @PostMapping("/get")
    public SaResult getShops(Shop.GetDTO dto) {
        log.info("getShops Test");
        log.info(String.valueOf(dto));
        var result = shopService.getShops(dto);
        return SaResult.ok("success").setData(new JSONObject().fluentPut("shops", result));
    }

    @ApiOperation(value = "添加店铺", notes = "",
            httpMethod = "POST")
    @PostMapping("/add")
    public SaResult addShop(Shop.AddDTO dto) {
        log.info("addShop Test");
        log.info(String.valueOf(dto));
        var code = shopService.addShop(dto);

        String data = switch (code) {
            case 1 -> "添加成功";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "删除店铺", notes = "",
            httpMethod = "POST")
    @PostMapping("/delete")
    public SaResult deleteShop(Shop.DeleteDTO dto) {
        log.info("deleteShop Test");
        log.info(String.valueOf(dto));
        var code = shopService.deleteShopByName(dto.getName());

        String data = switch (code) {
            case 1 -> "删除成功";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

}
