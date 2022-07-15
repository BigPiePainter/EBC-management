package com.pofa.ebcadmin.userLogin.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pofa.ebcadmin.userLogin.dao.ProductDao;
import com.pofa.ebcadmin.userLogin.dto.Product;
import com.pofa.ebcadmin.userLogin.entity.ProductInfo;
import com.pofa.ebcadmin.userLogin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    public ProductDao productDao;

    @Autowired
    public ProductInfo productInfo;


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int productAdd(Product.AddDTO dto) {

        QueryWrapper<ProductInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id", dto.getId());
        List<ProductInfo> productInfos = productDao.selectList(wrapper);
        if (productInfos.isEmpty()) {
            return productDao.insert(productInfo.setId(dto.getId())
                    .setDepartment(dto.getDepartment())
                    .setGroupName(dto.getGroupName())
                    .setOwner(dto.getOwner())
                    .setShopName(dto.getShopName())
                    .setProductName(dto.getProductName())
                    .setFirstCategory(dto.getFirstCategory())
                    .setProductDeduction(dto.getProductDeduction())
                    .setProductInsurance(dto.getProductInsurance())
                    .setProductFreight(dto.getProductFreight())
                    .setExtraRatio(dto.getExtraRatio())
                    .setFreightToPayment(dto.getFreightToPayment())
                    .setTransportWay(dto.getTransportWay())
                    .setStorehouse(dto.getStorehouse())
                    .setManufacturerName(dto.getManufacturerName())
                    .setManufacturerGroup(dto.getManufacturerGroup())
                    .setManufacturerPaymentMethod(dto.getManufacturerPaymentMethod())
                    .setManufacturerPaymentName(dto.getManufacturerPaymentName())
                    .setManufacturerPaymentId(dto.getManufacturerPaymentId())
                    .setManufacturerRecipient(dto.getManufacturerRecipient())
                    .setManufacturerPhone(dto.getManufacturerPhone())
                    .setManufacturerAddress(dto.getManufacturerAddress()));
        }
        return -100;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public JSONObject productGet(Product.GetDTO dto) {
        //分页查询待优化，暂时不需要
        //QueryWrapper<ProductInfo> wrapper = new QueryWrapper<>();
        Page<ProductInfo> page = new Page<>(dto.getPage(), dto.getItemsPerPage());
        productDao.selectPage(page, null);
        return new JSONObject().fluentPut("products", page.getRecords()).fluentPut("total", page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public JSONObject categoryGet() {
        var data = new JSONObject();

        //有提升空间，暂时不需要

        var targets = new ArrayList<String>();
        targets.add("department");
        targets.add("group_name");
        targets.add("shop_name");
        targets.add("first_category");
        targets.add("transport_way");
        targets.add("manufacturer_name");
        targets.add("manufacturer_payment_method");

        List<ProductInfo> results;
        for (var col : targets) {
            var array = new JSONArray();
            results = productDao.selectList(new QueryWrapper<ProductInfo>().select(col).groupBy(col));
            results.forEach(item -> {
                array.add(switch (col) {
                    case "department" -> item.getDepartment();
                    case "group_name" -> item.getGroupName();
                    case "shop_name" -> item.getShopName();
                    case "first_category" -> item.getFirstCategory();
                    case "transport_way" -> item.getTransportWay();
                    case "manufacturer_name" -> item.getManufacturerName();
                    case "manufacturer_payment_method" -> item.getManufacturerPaymentMethod();
                    default -> "ERROR";
                });
            });
            data.put(switch (col) {
                case "group_name" -> "groupName";
                case "shop_name" -> "shopName";
                case "first_category" -> "firstCategory";
                case "transport_way" -> "transportWay";
                case "manufacturer_name" -> "manufacturerName";
                case "manufacturer_payment_method" -> "manufacturerPaymentMethod";
                default -> col;
            }, array);
        }

        System.out.println(data);
        return data;
    }
}
