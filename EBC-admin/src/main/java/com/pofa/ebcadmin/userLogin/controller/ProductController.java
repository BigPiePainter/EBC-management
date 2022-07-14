package com.pofa.ebcadmin.userLogin.controller;


import cn.dev33.satoken.util.SaResult;
import com.pofa.ebcadmin.userLogin.dto.ProductAddDTO;
import com.pofa.ebcadmin.userLogin.dto.SysUserLoginDTO;
import com.pofa.ebcadmin.userLogin.service.ProductService;
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

import java.math.BigDecimal;

@Api(tags = "商品信息")
@Controller
@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    public ProductService productService;

    @ApiOperation(value = "新增商品", notes = "新增产品信息", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商品ID", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "554", required = false),
            @ApiImplicitParam(name = "department", value = "事业部", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
            @ApiImplicitParam(name = "group_name", value = "组别", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
            @ApiImplicitParam(name = "owner", value = "持品人", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
            @ApiImplicitParam(name = "shop_name", value = "店铺名", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
            @ApiImplicitParam(name = "first_category", value = "一级类目", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
            @ApiImplicitParam(name = "product_name", value = "产品名", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
            @ApiImplicitParam(name = "product_deduction", value = "品类扣点", dataType = "Double", paramType = "query", dataTypeClass = BigDecimal.class, example = "123456", required = false),
            @ApiImplicitParam(name = "product_insurance", value = "品类运费险", dataType = "Double", paramType = "query", dataTypeClass = BigDecimal.class, example = "123456", required = false),
            @ApiImplicitParam(name = "product_freight", value = "每单运费", dataType = "Double", paramType = "query", dataTypeClass = BigDecimal.class, example = "123456", required = false),
            @ApiImplicitParam(name = "extra_ratio", value = "子/主订单附带比", dataType = "Double", paramType = "query", dataTypeClass = BigDecimal.class, example = "123456", required = false),
            @ApiImplicitParam(name = "freight_to_payment", value = "运费/总货款", dataType = "Double", paramType = "query", dataTypeClass = BigDecimal.class, example = "123456", required = false),
            @ApiImplicitParam(name = "transport_way", value = "发货方式", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
            @ApiImplicitParam(name = "storehouse", value = "聚水潭仓库", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
            @ApiImplicitParam(name = "manufacturer_name", value = "厂家名", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
            @ApiImplicitParam(name = "manufacturer_group", value = "厂家群名", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
            @ApiImplicitParam(name = "manufacturer_payment_method", value = "厂家收款方式", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
            @ApiImplicitParam(name = "manufacturer_payment_name", value = "厂家收款人", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
            @ApiImplicitParam(name = "manufacturer_payment_id", value = "厂家收款账户号码", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
            @ApiImplicitParam(name = "manufacturer_recipient", value = "厂家退货-收件人", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
            @ApiImplicitParam(name = "manufacturer_phone", value = "厂家退货-收件手机号", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
            @ApiImplicitParam(name = "manufacturer_address", value = "厂家退货-收件地址", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "123456", required = false),
 })
    @PostMapping("/add")
    public SaResult userRegist(ProductAddDTO product) {
        int code = productService.productAdd(
                product.getId(),
                product.getDepartment(),
                product.getGroup_name(),
                product.getOwner(),
                product.getShop_name(),
                product.getProduct_name(),
                product.getFirst_category(),
                product.getProduct_deduction(),
                product.getProduct_insurance(),
                product.getProduct_freight(),
                product.getExtra_ratio(),
                product.getFreight_to_payment(),
                product.getTransport_way(),
                product.getStorehouse(),
                product.getManufacturer_name(),
                product.getManufacturer_group(),
                product.getManufacturer_payment_method(),
                product.getManufacturer_payment_name(),
                product.getManufacturer_payment_id(),
                product.getManufacturer_recipient(),
                product.getManufacturer_phone(),
                product.getManufacturer_address());

        String data = switch (code) {
            case 1 -> "创建成功";
            case -100 -> "当前商品ID已存在";
            default -> "未知错误";
        };


        //return new JsonResponse(code, data);
        return SaResult.ok("success").setData(data).setCode(code);
    }
}
