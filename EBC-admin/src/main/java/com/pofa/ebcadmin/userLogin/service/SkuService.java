package com.pofa.ebcadmin.userLogin.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.userLogin.dto.Sku;
import com.pofa.ebcadmin.userLogin.entity.SkuInfo;
import com.pofa.ebcadmin.userLogin.entity.UserInfo;

import java.util.List;

public interface SkuService {
    int addSkusByProductId(Long productId, JSONArray skus);
    List<SkuInfo> getSkusByProductId(Long productId);

}
