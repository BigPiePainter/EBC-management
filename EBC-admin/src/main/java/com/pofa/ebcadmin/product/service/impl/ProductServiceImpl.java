package com.pofa.ebcadmin.product.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pofa.ebcadmin.product.dao.ProductDao;
import com.pofa.ebcadmin.product.dto.Product;
import com.pofa.ebcadmin.product.entity.ProductInfo;
import com.pofa.ebcadmin.product.service.ProductService;
import com.pofa.ebcadmin.utils.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    public ProductDao productDao;

    @Autowired
    public ProductInfo productInfo;


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int addProduct(Product.AddDTO dto) {

        var wrapper = new QueryWrapper<ProductInfo>().eq("id", dto.getId());
        var productInfos = productDao.selectList(wrapper);
        if (productInfos.isEmpty()) {
            return productDao.insert(productInfo
                    .setId(dto.getId())
                    .setDepartment(dto.getDepartment())
                    .setTeam(dto.getTeam())
                    .setOwner(dto.getOwner())
                    .setShopName(dto.getShopName())
                    .setProductName(dto.getProductName())
                    .setFirstCategory(dto.getFirstCategory())
                    .setTransportWay(dto.getTransportWay())
                    .setStorehouse(dto.getStorehouse())
                    .setNote(dto.getNote()));
        }
        return -100;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int editProduct(Product.EditDTO dto) {
        return productDao.update(productInfo
                        .setDepartment(dto.getDepartment())
                        .setTeam(dto.getTeam())
                        .setOwner(dto.getOwner())
                        .setShopName(dto.getShopName())
                        .setProductName(dto.getProductName())
                        .setFirstCategory(dto.getFirstCategory())
                        .setTransportWay(dto.getTransportWay())
                        .setStorehouse(dto.getStorehouse())
                        .setNote(dto.getNote()),
                new UpdateWrapper<ProductInfo>().eq("id", dto.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public JSONObject getProductsByUserIds(List<Long> users, Product.GetDTO dto) {

        //json格式的匹配规则：select类别匹配，search模糊匹配
        var match = JSON.parseObject(dto.getMatch(), JSONObject.class);
        var select = match.getJSONObject("select");
        var search = match.getJSONObject("search");
        System.out.println(select);
        System.out.println(search);
        System.out.println("------------------");

        //sql待优化，暂时不需要
        var wrapper = new QueryWrapper<ProductInfo>().in("owner", users);

        //类别删选
        for (Map.Entry<String, Object> entry : select.entrySet()) {
            var value = (JSONArray) (entry.getValue());
            if (value.isEmpty()) continue;
            var items = new ArrayList<String>();
            value.forEach(item -> items.add((String) item));
            wrapper.in(Convert.camelToUnderScore(entry.getKey()), items);
        }

        //模糊查找，可优化
        for (Map.Entry<String, Object> entry : search.entrySet()) {
            var value = (String) (entry.getValue());
            if (value.isEmpty()) continue;

            wrapper.like(Convert.camelToUnderScore(entry.getKey()), value);
        }


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
        targets.add("team");
        targets.add("owner");
        targets.add("shop_name");
        targets.add("first_category");
        targets.add("transport_way");

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
                case "team" -> item.getTeam();
                case "owner" -> item.getOwner();
                case "shop_name" -> item.getShopName();
                case "first_category" -> item.getFirstCategory();
                case "transport_way" -> item.getTransportWay();
                default -> "ERROR";
            }));
            data.put(Convert.underScoreToCamel(col), array);
        }

        System.out.println(data);
        return data;
    }

}
