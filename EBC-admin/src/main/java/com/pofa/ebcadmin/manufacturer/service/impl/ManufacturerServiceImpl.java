package com.pofa.ebcadmin.manufacturer.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pofa.ebcadmin.manufacturer.dao.ManufacturerDao;
import com.pofa.ebcadmin.manufacturer.dto.Manufacturer;
import com.pofa.ebcadmin.manufacturer.entity.ManufacturerInfo;
import com.pofa.ebcadmin.manufacturer.service.ManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ManufacturerServiceImpl implements ManufacturerService {

    @Autowired
    public ManufacturerDao manufacturerDao;

    @Autowired
    public ManufacturerInfo manufacturerInfo;


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int addManufacturer(Manufacturer.AddDTO dto) {
        return manufacturerDao.insert(manufacturerInfo
                .setProduct_id(dto.getProduct_id())
                .setStartTime(dto.getStartTime())
                .setManufacturerName(dto.getManufacturerName())
                .setManufacturerGroup(dto.getManufacturerGroup())
                .setManufacturerPaymentMethod(dto.getManufacturerPaymentMethod())
                .setManufacturerPaymentName(dto.getManufacturerPaymentName())
                .setManufacturerPaymentId(dto.getManufacturerPaymentId())
                .setManufacturerRecipient(dto.getManufacturerRecipient())
                .setManufacturerPhone(dto.getManufacturerPhone())
                .setManufacturerAddress(dto.getManufacturerAddress())
                .setNote(dto.getNote()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<ManufacturerInfo> getManufacturersByProductId(Long productId) {
        return manufacturerDao.selectList(new QueryWrapper<ManufacturerInfo>().eq("product_id", productId));
    }
}
