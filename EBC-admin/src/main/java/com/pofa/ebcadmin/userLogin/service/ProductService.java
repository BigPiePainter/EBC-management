package com.pofa.ebcadmin.userLogin.service;

import com.pofa.ebcadmin.userLogin.entity.UserInfo;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    int productAdd(String id, String department, String group_name, String owner, String shop_name, String product_name, String first_category, BigDecimal product_deduction, BigDecimal product_insurance, BigDecimal product_freight, BigDecimal extra_ratio, BigDecimal freight_to_payment, String transport_way, String storehouse, String manufacturer_name, String manufacturer_group, String manufacturer_payment_method, String manufacturer_payment_name, String manufacturer_payment_id, String manufacturer_recipient, String manufacturer_phone, String manufacturer_address);
}
