package com.pofa.ebcadmin.userLogin.entity;


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
@TableName("skus")
public class SkuInfo {
    private Long uid;
    private Long productId;
    private Long skuId;

    private String skuName;
    private BigDecimal skuPrice;
    private BigDecimal skuCost;

    private Date startTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT)
    private String note;
}