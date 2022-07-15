package com.pofa.ebcadmin.userLogin.service;

import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.userLogin.dto.Product;

public interface ProductService {
    int productAdd(Product.AddDTO dto);

    JSONObject productGet(Product.GetDTO dto);

    JSONObject categoryGet();

}
