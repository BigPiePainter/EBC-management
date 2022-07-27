package com.pofa.ebcadmin.userLogin.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mysql.cj.xdevapi.JsonArray;
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
    public int addSkus(JSONArray skus) {
        System.out.println(new Date().getTime());

        var list = new ArrayList<SkuInfo>();
        var code = 0;
        for (int i = 0; i < skus.size(); i++) {
            var sku = skus.getJSONArray(i);
            list.add(new SkuInfo()
                    .setProductId(sku.getLong(0))
                    .setSkuId(sku.getLong(1))
                    .setSkuName(sku.getString(2))
                    .setSkuPrice(sku.getBigDecimal(3))
                    .setSkuCost(sku.getBigDecimal(4))
                    .setStartTime(sku.getDate(5))
            );
            if ((i + 1) % 3000 == 0) {
                code = skuDao.insertBatchSomeColumn(list);
                list.clear();
            }
        }

        if (!list.isEmpty()) {
            code = skuDao.insertBatchSomeColumn(list);
        }
        return code;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<SkuInfo> getSkusByProductId(Long productId) {
        return skuDao.selectList(new QueryWrapper<SkuInfo>().eq("product_id", productId).eq("deprecated", false));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<SkuInfo> getDeprecatedSkusByProductId(Long productId) {
        return skuDao.selectList(new QueryWrapper<SkuInfo>().eq("deprecated", true).eq("product_id", productId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int deprecateSkuByUid(Long uid) {
        return skuDao.update(null, new UpdateWrapper<SkuInfo>().eq("uid", uid).set("deprecated", true).set("delete_time", new Date()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int deleteSkuByUid(Long uid) {
        return skuDao.delete(new QueryWrapper<SkuInfo>().eq("uid", uid));
    }
}
