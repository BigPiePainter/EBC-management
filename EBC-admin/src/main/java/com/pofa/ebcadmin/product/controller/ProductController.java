package com.pofa.ebcadmin.product.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.product.dto.Product;
import com.pofa.ebcadmin.product.service.ProductService;
import com.pofa.ebcadmin.userLogin.entity.UserInfo;
import com.pofa.ebcadmin.userLogin.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "商品信息")
@Controller
@RestController
@RequestMapping("product")
@Slf4j
public class ProductController {

    @Autowired
    public UserService userService;
    @Autowired
    public ProductService productService;

    @ApiOperation(value = "新增商品", notes = "新增产品信息", httpMethod = "POST")
    @PostMapping("/add")
    public SaResult productAdd(Product.AddDTO dto) {
        log.info("ADD TEST");
        int code = productService.addProduct(dto);

        String data = switch (code) {
            case 1 -> "创建成功";
            case -100 -> "当前商品ID已存在";
            case -101 -> "商品ID过长";
            default -> "未知错误";
        };

        //return new JsonResponse(code, data);
        return SaResult.ok("success").setData(data).setCode(code);
    }


    @ApiOperation(value = "修改商品信息", notes = "根据商品ID",
            httpMethod = "POST")
    @PostMapping("/modify")
    public SaResult editProduct(Product.EditDTO dto) {

        log.info("Product EDIT TEST");
        log.info(String.valueOf(dto));
        var code = productService.editProduct(dto);

        String data = switch (code) {
            case 1 -> "修改成功";
            case -1 -> "变化日期有冲突，需要先删除旧的";
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
        log.info("GET TEST");
        if (!StpUtil.isLogin()){
            return SaResult.ok("success").setData("用户未登录");
        }
        UserInfo user = (UserInfo) StpUtil.getSession().get("user");
        log.info(String.valueOf(user));
        log.info(String.valueOf(dto));

//        //查询所有下级User
//        log.info(String.valueOf(StpUtil.getLoginIdAsLong()));
//        var users = userService.getUserIdsWithinAuthorityById(StpUtil.getLoginIdAsLong());
//        log.info(String.valueOf(users));

        JSONObject data = productService.getProductsByUser(user, dto);



        return SaResult.ok("success").setData(data);
    }


//    @ApiOperation(value = "获取权限内的事业部, 组别, 商店名等类别", notes = "需要登陆", httpMethod = "POST")
//    @ApiImplicitParams({
//    })
//    @SaCheckLogin
//    @PostMapping("/getCategory")
//    public SaResult getCategory() {
//        log.info("getCategory TEST");
//
//        //查询所有下级User
//        log.info(String.valueOf(StpUtil.getLoginIdAsLong()));
//        var users = userService.getUserIdsWithinAuthorityById(StpUtil.getLoginIdAsLong());
//        log.info(String.valueOf(users));
//
//        JSONObject data = productService.getCategorysByUserIds(users);
//
//        return SaResult.ok("success").setData(data);
//    }

    @ApiOperation(value = "删除商品", notes = "将商品挪入回收站", httpMethod = "POST")
    @PostMapping("/delete")
    public SaResult deleteProduct(Product.deleteDTO dto) {
        log.info("deleteProduct TEST");

        var code = productService.deprecateProductById(dto.getId());

        log.info(String.valueOf(code));

        String data = switch (code) {
            case 1 -> "删除成功";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "获取未匹配商品信息", notes = "认领大厅", httpMethod = "POST")
    @PostMapping("/getMismatchProducts")
    public SaResult getMismatchProducts() {
        log.info("getMismatchProducts TEST");
        var mismatchProducts =  productService.getMismatchProducts();
        return SaResult.ok("success").setData(new JSONObject().fluentPut("mismatchProducts", mismatchProducts));
    }

//    @ApiOperation(value = "删除商品", notes = "需要登陆", httpMethod = "POST")
//    @ApiImplicitParams({
//    })
//    @SaCheckLogin
//    @PostMapping("/deleteProduct")
//    public SaResult deleteProduct() {
//        log.info("deleteProduct TEST");
//
//        //查询所有下级User
//        log.info(StpUtil.getLoginIdAsLong());
//        var users = userService.getUserIdsWithinAuthorityById(StpUtil.getLoginIdAsLong());
//        log.info(users);
//
//        JSONObject data = productService.getCategorysByUserIds(users);
//
//        return SaResult.ok("success").setData(data);
//    }
}
