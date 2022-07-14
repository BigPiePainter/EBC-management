package com.pofa.ebcadmin.userLogin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pofa.ebcadmin.userLogin.dao.ProductDao;
import com.pofa.ebcadmin.userLogin.dao.UserDao;
import com.pofa.ebcadmin.userLogin.entity.ProductInfo;
import com.pofa.ebcadmin.userLogin.entity.UserInfo;
import com.pofa.ebcadmin.userLogin.service.ProductService;
import com.pofa.ebcadmin.userLogin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    public ProductDao productDao;

    @Autowired
    public ProductInfo productInfo;


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int productAdd(String id, String department, String group_name, String owner, String shop_name, String product_name, String first_category, BigDecimal product_deduction, BigDecimal product_insurance, BigDecimal product_freight, BigDecimal extra_ratio, BigDecimal freight_to_payment, String transport_way, String storehouse, String manufacturer_name, String manufacturer_group, String manufacturer_payment_method, String manufacturer_payment_name, String manufacturer_payment_id, String manufacturer_recipient, String manufacturer_phone, String manufacturer_address) {

        QueryWrapper<ProductInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        List<ProductInfo> productInfos = productDao.selectList(wrapper);
        if (productInfos.isEmpty()) {
            return productDao.insert(productInfo.setId(id)
                    .setDepartment(department)
                    .setGroup_name(group_name)
                    .setOwner(owner)
                    .setShop_name(shop_name)
                    .setProduct_name(product_name)
                    .setFirst_category(first_category)
                    .setProduct_deduction(product_deduction)
                    .setProduct_insurance(product_insurance)
                    .setProduct_freight(product_freight)
                    .setExtra_ratio(extra_ratio)
                    .setFreight_to_payment(freight_to_payment)
                    .setTransport_way(transport_way)
                    .setStorehouse(storehouse)
                    .setManufacturer_name(manufacturer_name)
                    .setManufacturer_group(manufacturer_group)
                    .setManufacturer_payment_method(manufacturer_payment_method)
                    .setManufacturer_payment_name(manufacturer_payment_name)
                    .setManufacturer_payment_id(manufacturer_payment_id)
                    .setManufacturer_recipient(manufacturer_recipient)
                    .setManufacturer_phone(manufacturer_phone)
                    .setManufacturer_address(manufacturer_address));
        }
        return -100;
    }
}
