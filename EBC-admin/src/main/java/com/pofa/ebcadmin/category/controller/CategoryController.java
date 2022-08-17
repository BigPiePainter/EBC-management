package com.pofa.ebcadmin.category.controller;


import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.category.dto.Category;
import com.pofa.ebcadmin.category.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "一级类目")
@Controller
@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    public CategoryService categoryService;


    @ApiOperation(value = "添加一级类目信息", notes = "",
            httpMethod = "POST")
    @PostMapping("/add")
    public SaResult addCategory(Category.AddDTO dto) {
        System.out.println("addCategory Test");
        System.out.println(dto);
        var code = categoryService.addCategory(dto);

        String data = switch (code) {
            case 1 -> "添加成功";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "删除一级类目信息", notes = "",
            httpMethod = "POST")
    @PostMapping("/delete")
    public SaResult deleteCategory(Category.DeleteDTO dto) {
        System.out.println("deleteCategory Test");
        System.out.println(dto);
        var code = categoryService.deleteCategoryByUid(dto.getUid());

        String data = switch (code) {
            case 1 -> "删除成功";
            case -1 -> "该一级类目正在被使用，无法删除";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "删除一级类目信息", notes = "",
            httpMethod = "POST")
    @PostMapping("/deleteHistory")
    public SaResult deleteCategoryHistory(Category.DeleteHistoryDTO dto) {
        System.out.println("deleteCategoryHistory Test");
        System.out.println(dto);
        var code = categoryService.deleteCategoryHistoryByUid(dto.getUid());

        String data = switch (code) {
            case 1 -> "删除成功";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "修改一级类目信息", notes = "根据UID",
            httpMethod = "POST")
    @PostMapping("/modify")
    public SaResult editCategory(Category.EditDTO dto) {
        //List<ManufacturerInfo> userInfos = manufacturerService.userLogin(user.getUsername(), user.getPassword());
        var code = categoryService.editCategory(dto);

        String data = switch (code) {
            case 1 -> "修改成功";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "获取一级类目信息", notes = "",
            httpMethod = "POST")
    @PostMapping("/get")
    public SaResult getCategorys(Category.GetDTO dto) {
        System.out.println("getCategorys TEST");
        var categorys = categoryService.getCategorys(dto);
        return SaResult.ok("success").setData(new JSONObject().fluentPut("categorys", categorys));
    }


    @ApiOperation(value = "添加一级类目历史信息", notes = "",
            httpMethod = "POST")
    @PostMapping("/addHistory")
    public SaResult addCategoryHistory(Category.AddHistoryDTO dto) {
        System.out.println("addCategoryHistory Test");
        System.out.println(dto);
        var code = categoryService.addCategoryHistory(dto);

        String data = switch (code) {
            case 1 -> "添加成功";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "修改一级类目历史信息", notes = "根据UID",
            httpMethod = "POST")
    @PostMapping("/modifyHistory")
    public SaResult editCategoryHistory(Category.EditHistoryDTO dto) {
        var code = categoryService.editCategoryHistory(dto);

        String data = switch (code) {
            case 1 -> "修改成功";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "获取一级类目历史信息", notes = "",
            httpMethod = "POST")
    @PostMapping("/getHistorys")
    public SaResult getCategoryHistorys(Category.GetHistoryDTO dto) {
        System.out.println("getCategoryHistorys TEST");
        var categorys = categoryService.getCategoryHistorys(dto);
        return SaResult.ok("success").setData(new JSONObject().fluentPut("categoryHistorys", categorys));
    }


}
