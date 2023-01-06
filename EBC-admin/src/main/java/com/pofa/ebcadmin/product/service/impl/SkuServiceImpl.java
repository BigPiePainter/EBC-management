package com.pofa.ebcadmin.product.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pofa.ebcadmin.product.dao.SkuDao;
import com.pofa.ebcadmin.product.entity.SkuInfo;
import com.pofa.ebcadmin.product.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SkuServiceImpl implements SkuService {

    @Autowired
    public SkuDao skuDao;
    @Autowired
    public SkuInfo skuInfo;

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public JSONObject addSkus(JSONArray skus) {
        log.info(String.valueOf(new Date().getTime()));

        var list = new ArrayList<SkuInfo>();
        var count = 0;
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
        }

        if (!list.isEmpty()) {
            //商品ID去重
//            var skuWithinOtherProduct = skuDao.selectList(new QueryWrapper<SkuInfo>().in("sku_id", list.stream().map(SkuInfo::getSkuId).toList()).ne("product_id", skus.getJSONArray(0).getLong(0)));
//            if (skuWithinOtherProduct.size() > 0){
//                return new JSONObject().fluentPut("error", "导入失败，相同的SKUID不能出现在两个商品中");
//            }
            count += skuDao.insertBatchSomeColumn(list);
        }

        var deleteCount = skuDao.deleteUnusedSkuInfos(skus.getJSONArray(0).getLong(0));
        return new JSONObject().fluentPut("add", count).fluentPut("delete", deleteCount);
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
    public int deprecateSkuByUids(String uids) {
        var _uids = uids.split(",");
        return skuDao.update(null, new UpdateWrapper<SkuInfo>().in("uid", List.of(_uids)).set("deprecated", true));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int deleteSkuByUid(Long uid) {
        return skuDao.delete(new QueryWrapper<SkuInfo>().eq("uid", uid));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int deleteSkuByUids(String uids) {
        var _uids = uids.split(",");
        return skuDao.delete(new QueryWrapper<SkuInfo>().in("uid", List.of(_uids)));
    }
}
