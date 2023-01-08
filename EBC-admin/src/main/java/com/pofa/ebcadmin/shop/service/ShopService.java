package com.pofa.ebcadmin.shop.service;

import com.pofa.ebcadmin.shop.dto.Shop;
import com.pofa.ebcadmin.shop.entity.ShopInfo;

import java.util.List;

public interface ShopService {
    List<ShopInfo> getShops(Shop.GetDTO dto);

    int addShop(Shop.AddDTO dto);

    int deleteShopByName(String name);


}
