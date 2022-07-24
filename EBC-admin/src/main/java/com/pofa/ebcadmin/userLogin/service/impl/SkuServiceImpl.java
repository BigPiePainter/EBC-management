package com.pofa.ebcadmin.userLogin.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pofa.ebcadmin.userLogin.dao.SkuDao;
import com.pofa.ebcadmin.userLogin.entity.SkuInfo;
import com.pofa.ebcadmin.userLogin.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
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

        System.out.println(new Date().getTime());

        System.out.println("---------------------------------------\n-------------------");
        var list = new ArrayList<SkuInfo>();
        for (var item : skus) {
            var sku = (JSONArray) item;

            list.add(new SkuInfo()
                    .setProductId(sku.getLong(0))
                    .setSkuId(sku.getLong(1))
                    .setSkuName(sku.getString(2))
                    .setSkuPrice(sku.getBigDecimal(3))
                    .setSkuCost(sku.getBigDecimal(4))
                    .setStartTime(sku.getDate(5))
            );
        }

        System.out.println(new Date().getTime());

        skuDao.insertBatchSomeColumn(list);
        System.out.println(new Date().getTime());

        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<SkuInfo> getSkusByProductId(Long productId) {
        return skuDao.selectList(new QueryWrapper<SkuInfo>().eq("product_id", productId));
    }
}
