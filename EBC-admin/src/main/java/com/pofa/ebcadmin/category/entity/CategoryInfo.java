package com.pofa.ebcadmin.category.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@Repository
@TableName("categorys")
public class CategoryInfo {
    private Long uid;

    private String name;

    @TableField(fill = FieldFill.INSERT)
    private String note;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;

    @TableField(fill = FieldFill.INSERT)
    private Boolean deprecated;


}
