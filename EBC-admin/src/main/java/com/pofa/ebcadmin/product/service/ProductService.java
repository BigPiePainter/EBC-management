package com.pofa.ebcadmin.product.service;

import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.product.dto.Product;

import java.util.List;

public interface ProductService {
    int addProduct(Product.AddDTO dto);

    int editProduct(Product.EditDTO dto);

    JSONObject getProductsByUserIds(List<Long> idList, Product.GetDTO dto);

    JSONObject getCategorysByUserIds(List<Long> idList);

    int deprecateProductById(Long uid);

}
