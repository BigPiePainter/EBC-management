package com.pofa.ebcadmin.order.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@Repository
@TableName("orders")
public class OrderInfo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;
    private String paymentId;
    private BigDecimal amount;
    private BigDecimal postage;
    private BigDecimal totalAmount;
    private BigDecimal actualTotalAmount;
    private BigDecimal actualAmount;
    private Integer orderStatus;
    private Date orderSetupTime;
    private Date orderPaymentTime;
    private String productTitle;
    private Long productCount;
    private String expressNumber;
    private String expressCompany;
    private Long shopId;
    private String shopName;
    private Long supplierId;
    private String supplierName;
    private Integer storehouseType;
    private BigDecimal refundAmount;
    private String skuName;
    private String sellerCode;
    private Long productId;


    @TableField(exist = false)
    private BigDecimal productTotalAmount;

}
