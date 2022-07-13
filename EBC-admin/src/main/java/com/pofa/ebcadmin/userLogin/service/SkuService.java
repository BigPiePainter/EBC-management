package com.pofa.ebcadmin.userLogin.service;

import com.pofa.ebcadmin.userLogin.entity.SkuInfo;

import java.util.List;

public interface SkuService {
    int skuAdd(
            String product_id,
            String sku_id,
            String sku_name,
            Double price,
            Double cost,
            Double start_time,
            Double end_time
    );

    int skuDelete(String sku_id);

    int skuEdit(
            String product_id,
            String sku_id,
            String sku_name,
            Double price,
            Double cost,
            Double start_time,
            Double end_time
    );

    List<SkuInfo> skuReturn(String product_id);

}
