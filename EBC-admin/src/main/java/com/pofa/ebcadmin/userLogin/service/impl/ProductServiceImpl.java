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

        var wrapper = new QueryWrapper<ProductInfo>().eq("id", dto.getId());
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
    public JSONObject getProductsByUserIds(List<Long> users, Product.GetDTO dto) {
        //sql待优化，暂时不需要
        var wrapper = new QueryWrapper<ProductInfo>().in("owner", users);

        var page = new Page<ProductInfo>(dto.getPage(), dto.getItemsPerPage());
        productDao.selectPage(page, wrapper);
        return new JSONObject().fluentPut("products", page.getRecords()).fluentPut("total", page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public JSONObject getCategorysByUserIds(List<Long> users) {
        var data = new JSONObject();

        //有提升空间，暂时不需要

        var targets = new ArrayList<String>();
        targets.add("department");
        targets.add("group_name");
        targets.add("owner");
        targets.add("shop_name");
        targets.add("first_category");
        targets.add("transport_way");
        targets.add("manufacturer_name");
        targets.add("manufacturer_payment_method");

        List<ProductInfo> results;
        for (var col : targets) {
            var array = new JSONArray();

//            var wrapper = new QueryWrapper<ProductInfo>().select(col).groupBy(col).and(i -> {
//                for (Long id : users) i.eq("owner", id).or();
//            });

            var wrapper = new QueryWrapper<ProductInfo>().select(col).groupBy(col).in("owner", users);


            results = productDao.selectList(wrapper);
            results.forEach(item -> array.add(switch (col) {
                case "department" -> item.getDepartment();
                case "group_name" -> item.getGroupName();
                case "owner" -> item.getOwner();
                case "shop_name" -> item.getShopName();
                case "first_category" -> item.getFirstCategory();
                case "transport_way" -> item.getTransportWay();
                case "manufacturer_name" -> item.getManufacturerName();
                case "manufacturer_payment_method" -> item.getManufacturerPaymentMethod();
                default -> "ERROR";
            }));
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
