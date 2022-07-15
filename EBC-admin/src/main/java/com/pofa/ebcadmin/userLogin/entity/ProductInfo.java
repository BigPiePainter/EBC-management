package com.pofa.ebcadmin.userLogin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;


@Data
@Accessors(chain = true)
@Repository
@TableName("products")
public class ProductInfo {
    private Long uid;

    private String id;
    private String department;
    private String groupName;
    private String owner;
    private String shopName;
    private String firstCategory;

    private String productName;
    private BigDecimal productDeduction;
    private BigDecimal productInsurance;
    private BigDecimal productFreight;

    private BigDecimal extraRatio;
    private BigDecimal freightToPayment;
    private String transportWay;
    private String storehouse;

    private String manufacturerName;
    private String manufacturerGroup;
    private String manufacturerPaymentMethod;
    private String manufacturerPaymentName;
    private String manufacturerPaymentId;
    private String manufacturerRecipient;
    private String manufacturerPhone;
    private String manufacturerAddress;
}