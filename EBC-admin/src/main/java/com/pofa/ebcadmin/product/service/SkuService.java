package com.pofa.ebcadmin.product.service;

import com.alibaba.fastjson2.JSONArray;
import com.pofa.ebcadmin.product.entity.SkuInfo;

import java.util.List;

public interface SkuService {
    int addSkus(JSONArray skus);
    List<SkuInfo> getSkusByProductId(Long productId);

    List<SkuInfo> getDeprecatedSkusByProductId(Long productId);

    int deprecateSkuByUids(String uids);

    int deleteSkuByUid(Long uid);

    int deleteSkuByUids(String uids);
}
