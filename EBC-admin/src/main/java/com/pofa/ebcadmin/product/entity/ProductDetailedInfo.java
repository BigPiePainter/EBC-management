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
public class ProductDetailedInfo {
    private Long id;
    private Long department;
    private Long team;
    private Long owner;
    private String shopName;
    private Long categoryId;
    private BigDecimal deduction;
    private BigDecimal insurance;
    private String productName;
    private String transportWay;
    private String storehouse;
    private String productNote;
    private Boolean deprecated;
    private String manufacturerName;
    private String manufacturerGroup;
    private String manufacturerPaymentMethod;
    private String manufacturerPaymentName;
    private String manufacturerPaymentId;
    private String manufacturerRecipient;
    private String manufacturerPhone;
    private String manufacturerAddress;
    private BigDecimal freight;
    private BigDecimal extraRatio;
    private BigDecimal freightToPayment;
    private String manufacturerNote;
}