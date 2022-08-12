package com.pofa.ebcadmin.manufacturer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pofa.ebcadmin.manufacturer.dao.ManufacturerDao;
import com.pofa.ebcadmin.manufacturer.dto.Manufacturer;
import com.pofa.ebcadmin.manufacturer.entity.ManufacturerInfo;
import com.pofa.ebcadmin.manufacturer.service.ManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
                .setProductId(dto.getProductId())
                .setStartTime(dto.getStartTime())
                .setManufacturerName(dto.getManufacturerName())
                .setManufacturerGroup(dto.getManufacturerGroup())
                .setManufacturerPaymentMethod(dto.getManufacturerPaymentMethod())
                .setManufacturerPaymentName(dto.getManufacturerPaymentName())
                .setManufacturerPaymentId(dto.getManufacturerPaymentId())
                .setManufacturerRecipient(dto.getManufacturerRecipient())
                .setManufacturerPhone(dto.getManufacturerPhone())
                .setManufacturerAddress(dto.getManufacturerAddress())
                .setFreight(dto.getFreight())
                .setExtraRatio(dto.getExtraRatio())
                .setFreightToPayment(dto.getFreightToPayment())
                .setNote(dto.getNote()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int editManufacturer(Manufacturer.EditDTO dto) {
        return manufacturerDao.update(manufacturerInfo
                        .setProductId(dto.getProductId())
                        .setStartTime(dto.getStartTime())
                        .setManufacturerName(dto.getManufacturerName())
                        .setManufacturerGroup(dto.getManufacturerGroup())
                        .setManufacturerPaymentMethod(dto.getManufacturerPaymentMethod())
                        .setManufacturerPaymentName(dto.getManufacturerPaymentName())
                        .setManufacturerPaymentId(dto.getManufacturerPaymentId())
                        .setManufacturerRecipient(dto.getManufacturerRecipient())
                        .setManufacturerPhone(dto.getManufacturerPhone())
                        .setManufacturerAddress(dto.getManufacturerAddress())
                        .setFreight(dto.getFreight())
                        .setExtraRatio(dto.getExtraRatio())
                        .setFreightToPayment(dto.getFreightToPayment())
                        .setNote(dto.getNote()),
                new UpdateWrapper<ManufacturerInfo>().eq("uid", dto.getUid()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<ManufacturerInfo> getManufacturersByProductId(Long productId) {
        return manufacturerDao.selectList(new QueryWrapper<ManufacturerInfo>().eq("product_id", productId).eq("deprecated", false));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int deprecateManufacturersByUid(Long uid) {
        return manufacturerDao.update(null, new UpdateWrapper<ManufacturerInfo>().eq("uid", uid).set("deprecated", true).set("modify_time", new Date()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int deleteManufacturersByUid(Long uid) {
        return manufacturerDao.delete(new QueryWrapper<ManufacturerInfo>().eq("uid", uid));
    }
}
