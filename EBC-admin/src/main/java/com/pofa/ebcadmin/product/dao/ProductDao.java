package com.pofa.ebcadmin.product.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.pofa.ebcadmin.product.entity.ProductInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductDao extends BaseMapper<ProductInfo> {

    //哪个更快?????
    //@Select("SELECT * from products where id in (SELECT products.id FROM products WHERE (SELECT COUNT(1) FROM skus WHERE products.id = skus.product_id ) = 0)")
    @Select("SELECT * from products where id in (SELECT products.id FROM products LEFT JOIN skus on products.id = skus.product_id where skus.product_id is NULL)")
    List<ProductInfo> selectProductsWithoutSku(@Param(Constants.WRAPPER) Wrapper<ProductInfo> queryWrapper);
}
