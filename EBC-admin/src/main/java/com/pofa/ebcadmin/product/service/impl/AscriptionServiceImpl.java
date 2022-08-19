package com.pofa.ebcadmin.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pofa.ebcadmin.product.dao.AscriptionDao;
import com.pofa.ebcadmin.product.entity.AscriptionInfo;
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


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<AscriptionInfo> getAscriptionInfosByProductId(Long productId) {
        return ascriptionDao.selectList(new QueryWrapper<AscriptionInfo>().eq("product", productId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int deleteAscriptionInfoByUid(Long uid) {
        return ascriptionDao.delete(new QueryWrapper<AscriptionInfo>().eq("uid", uid));
    }
}
