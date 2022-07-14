package com.pofa.ebcadmin.userLogin.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductAddDTO {
    private String id;
    private String department;
    private String group_name;
    private String owner;
    private String shop_name;
    private String first_category;
    private String product_name;
    private BigDecimal product_deduction;
    private BigDecimal product_insurance;
    private BigDecimal product_freight;
    private BigDecimal extra_ratio;
    private BigDecimal freight_to_payment;
    private String transport_way;
    private String storehouse;
    private String manufacturer_name;
    private String manufacturer_group;
    private String manufacturer_payment_method;
    private String manufacturer_payment_name;
    private String manufacturer_payment_id;
    private String manufacturer_recipient;
    private String manufacturer_phone;
    private String manufacturer_address;
}
