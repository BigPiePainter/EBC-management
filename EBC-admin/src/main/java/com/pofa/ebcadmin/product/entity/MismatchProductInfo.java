package com.pofa.ebcadmin.product.entity;

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
@TableName("mismatchproducts")
public class MismatchProductInfo {
    private Long id;
    private String productTitle;

    @TableField(exist = false)
    private BigDecimal totalAmount;
}