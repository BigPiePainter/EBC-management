package com.pofa.ebcadmin.product.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Data
@Accessors(chain = true)
@Repository
@TableName("products")
public class ProductInfo {
    private Long id;
    private Long owner;

    private Long department;
    private Long team;

    private Long firstCategory;

    private String shopName;

    private String productName;


    private String transportWay;
    private String storehouse;


    @TableField(fill = FieldFill.INSERT)
    private String note;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;

    @TableField(fill = FieldFill.INSERT)
    private Boolean deprecated;
}