package com.pofa.ebcadmin.product.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pofa.ebcadmin.product.entity.ProductInfo;
import com.pofa.ebcadmin.profitReport.entity.MismatchedSkusInfo;
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


    @Select("""
            SELECT id, owner, department, team, shop_name, first_category, product_name, transport_way, storehouse, end_time, note, create_time, modify_time, deprecated, skus, manufacturers FROM products 
            LEFT JOIN (select product_id, count(*) as skus from skus group by product_id) skus_count on products.id = skus_count.product_id 
            LEFT JOIN (select product_id, count(*) as manufacturers from manufacturers group by product_id) manufacturers_count on products.id = manufacturers_count.product_id
            ${ew.customSqlSegment}
            """)
    List<ProductInfo> selectProductsWithSkuCountWithPage(Page<ProductInfo> page, @Param(Constants.WRAPPER) Wrapper<ProductInfo> queryWrapper);
}
