package com.pofa.ebcadmin.userLogin.service;

import com.alibaba.fastjson2.JSONArray;
import com.pofa.ebcadmin.userLogin.entity.SkuInfo;

import java.util.List;

public interface SkuService {
    int addSkus(JSONArray skus);
    List<SkuInfo> getSkusByProductId(Long productId);

    List<SkuInfo> getDeprecatedSkusByProductId(Long productId);

    int deprecateSkuByUid(Long uid);

    int deleteSkuByUid(Long uid);
}
