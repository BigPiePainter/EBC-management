package com.pofa.ebcadmin.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pofa.ebcadmin.product.dao.AscriptionDao;
import com.pofa.ebcadmin.product.dao.ProductDao;
import com.pofa.ebcadmin.product.entity.AscriptionInfo;
import com.pofa.ebcadmin.product.entity.ProductInfo;
import com.pofa.ebcadmin.product.service.AscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AscriptionServiceImpl implements AscriptionService {

    @Autowired
    public AscriptionDao ascriptionDao;
    @Autowired
    public AscriptionInfo ascriptionInfo;

    @Autowired
    public ProductDao productDao;

    @Autowired
    public ProductInfo productInfo;


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<AscriptionInfo> getAscriptionInfosByProductId(Long productId) {
        return ascriptionDao.selectList(new QueryWrapper<AscriptionInfo>().eq("product", productId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int deleteAscriptionInfoByUid(Long uid) {

        var info = ascriptionDao.selectList(new QueryWrapper<AscriptionInfo>().eq("uid", uid)).get(0);

        //删除归属信息
        ascriptionDao.delete(new QueryWrapper<AscriptionInfo>().eq("uid", uid));

        var newInfo = ascriptionDao.selectList(
                new QueryWrapper<AscriptionInfo>()
                        .select("department", "team", "owner")
                        .eq("product", info.getProduct())
                        .orderByDesc("start_time")
                        .last("limit 1")
        ).get(0);

        productDao.update(new ProductInfo()
                        .setDepartment(newInfo.getDepartment())
                        .setTeam(newInfo.getTeam())
                        .setOwner(newInfo.getOwner()),
                new UpdateWrapper<ProductInfo>().eq("id", info.getProduct()));

        return 1;
    }
}
