package com.pofa.ebcadmin.userLogin.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pofa.ebcadmin.userLogin.dao.SkuDao;
import com.pofa.ebcadmin.userLogin.dto.Sku;
import com.pofa.ebcadmin.userLogin.entity.SkuInfo;
import com.pofa.ebcadmin.userLogin.entity.UserInfo;
import com.pofa.ebcadmin.userLogin.service.SkuService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    public SkuDao skuDao;

    @Autowired
    public SkuInfo skuInfo;

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int addSkusByProductId(Long productId, JSONArray skus) {
        System.out.println("---------------------------------------\n-------------------");
        System.out.println(productId);
        System.out.println(skus);

        for (var item: skus) {
            var sku = (JSONArray) item;


            System.out.println(sku);
        }


        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<SkuInfo> getSkusByProductId(Long productId) {
        return skuDao.selectList(new QueryWrapper<SkuInfo>().eq("product_id", productId));
    }
}
