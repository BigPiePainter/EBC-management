package com.pofa.ebcadmin.product.service;

import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.product.dto.Product;
import com.pofa.ebcadmin.product.entity.MismatchProductInfo;
import com.pofa.ebcadmin.user.entity.UserInfo;

import java.util.List;

public interface ProductService {
    int addProduct(Product.AddDTO dto);

    int editProduct(Product.EditDTO dto);

//    JSONObject getProductsByUserIds(List<Long> idList, Product.GetDTO dto);

    JSONObject getProductsByUser(UserInfo user, Product.GetDTO dto, boolean deprecated);

    JSONObject getAllProducts(Product.GetDTO dto);


//    JSONObject getCategorysByUserIds(List<Long> idList);


    int deprecateProductById(Long id);

    int restoreProductById(Long id);

    int deleteProductById(Long id);

    List<MismatchProductInfo> getMismatchProducts();


    JSONObject productSynchronization(Long productIdA, Long productIdB);

}
