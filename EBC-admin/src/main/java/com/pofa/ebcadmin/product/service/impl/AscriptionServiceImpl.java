package com.pofa.ebcadmin.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pofa.ebcadmin.product.dao.AscriptionDao;
import com.pofa.ebcadmin.product.entity.AscriptionInfo;
import com.pofa.ebcadmin.product.service.AscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AscriptionServiceImpl implements AscriptionService {

    @Autowired
    public AscriptionDao ascriptionDao;
    @Autowired
    public AscriptionInfo ascriptionInfo;


    @Override
    public int addAscriptionInfo() {
        return ascriptionDao.insert(ascriptionInfo
                .setProduct(1L)
                .setOwner(1L)
                .setDepartment(1L)
                .setTeam(1L)
                .setStartTime(new Date())
                .setNote("c")
        );
    }

    @Override
    public List<AscriptionInfo> getAscriptionInfosByProductId(Long productId) {
        return ascriptionDao.selectList(new QueryWrapper<AscriptionInfo>().eq("product", productId));
    }

    @Override
    public int deleteAscriptionInfoByUid(Long uid) {
        return 0;
    }
}
