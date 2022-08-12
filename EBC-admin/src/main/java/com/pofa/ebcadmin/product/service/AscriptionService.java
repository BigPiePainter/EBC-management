package com.pofa.ebcadmin.product.service;

import com.pofa.ebcadmin.product.entity.AscriptionInfo;

import java.util.List;

public interface AscriptionService {
    int addAscriptionInfo();

    List<AscriptionInfo> getAscriptionInfosByProductId(Long productId);

    int deleteAscriptionInfoByUid(Long uid);
}
