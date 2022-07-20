package com.pofa.ebcadmin.userLogin.service;

import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.userLogin.dto.Product;

import java.util.List;

public interface ProductService {
    int productAdd(Product.AddDTO dto);

    JSONObject getProductsByUserIds(List<Long> idList, Product.GetDTO dto);

    JSONObject getCategorysByUserIds(List<Long> idList);

}
