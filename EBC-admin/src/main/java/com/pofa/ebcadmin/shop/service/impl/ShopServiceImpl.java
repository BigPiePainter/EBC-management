package com.pofa.ebcadmin.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pofa.ebcadmin.product.dao.ProductDao;
import com.pofa.ebcadmin.product.entity.ProductInfo;
import com.pofa.ebcadmin.shop.dao.ShopDao;
import com.pofa.ebcadmin.shop.dto.Shop;
import com.pofa.ebcadmin.shop.entity.ShopInfo;
import com.pofa.ebcadmin.shop.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    public ShopDao shopDao;
    @Autowired
    public ShopInfo shopInfo;


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<ShopInfo> getShops(Shop.GetDTO dto) {
        return shopDao.selectList(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int addShop(Shop.AddDTO dto) {
        return shopDao.insert(shopInfo
                .setName(dto.getName())
                .setNote(dto.getNote()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int deleteShopByName(String name) {
        return shopDao.delete(new QueryWrapper<ShopInfo>().eq("name", name));
    }

}
