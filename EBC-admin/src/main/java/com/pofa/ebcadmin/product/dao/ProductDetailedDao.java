package com.pofa.ebcadmin.product.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pofa.ebcadmin.product.entity.ProductDetailedInfo;
import com.pofa.ebcadmin.product.entity.ProductInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductDetailedDao extends BaseMapper<ProductDetailedInfo> {

    //哪个更快?????
    //@Select("SELECT * from products where id in (SELECT products.id FROM products WHERE (SELECT COUNT(1) FROM skus WHERE products.id = skus.product_id ) = 0)")
    @Select("""
            WITH product_ascription AS (
              SELECT
                product_id,
                department,
                team,
                owner
              from
                (
                  SELECT
                    *,
                    ROW_NUMBER() OVER (
                      PARTITION BY product_id
                      ORDER BY
                        start_time DESC
                    ) AS num
                  FROM
                    pofa.ascriptions
                  where
                    start_time <= ${monthDate}
                ) a
              where
                num = 1
            ),
            manufacturers AS (
              SELECT
                *
              from
                (
                  SELECT
                    *,
                    ROW_NUMBER() OVER (
                      PARTITION BY product_id
                      ORDER BY
                        start_time DESC
                    ) AS num
                  FROM
                    pofa.manufacturers
                  where
                    start_time <= ${monthDate}
                ) a
              where
                num = 1
            ),
            first_category AS (
              SELECT
                a.category_id,
                deduction,
                insurance,
                a.start_time
              from
                (
                  SELECT
                    *,
                    ROW_NUMBER() OVER (
                      PARTITION BY category_id
                      ORDER BY
                        start_time DESC
                    ) AS num
                  FROM
                    pofa.categoryhistorys
                  where
                    start_time <= ${monthDate}
                ) a
              where
                num = 1
            )
            select
              products.id,
              product_ascription.department,
              product_ascription.team,
              product_ascription.owner,
              products.shop_name,
              first_category.category_id,
              first_category.deduction,
              first_category.insurance,
              products.product_name,
              products.transport_way,
              products.storehouse,
              products.note as product_note,
              products.deprecated,
              manufacturers.manufacturer_name,
              manufacturers.manufacturer_group,
              manufacturers.manufacturer_payment_method,
              manufacturers.manufacturer_payment_name,
              manufacturers.manufacturer_payment_id,
              manufacturers.manufacturer_recipient,
              manufacturers.manufacturer_phone,
              manufacturers.manufacturer_address,
              manufacturers.freight,
              manufacturers.extra_ratio,
              manufacturers.freight_to_payment,
              manufacturers.note as manufacturer_note
            from
              pofa.products
              left join product_ascription on pofa.products.id = product_ascription.product_id
              left join manufacturers on pofa.products.id = manufacturers.product_id
              left join first_category on pofa.products.first_category = first_category.category_id
            """)
    List<ProductDetailedInfo> getAllDetailedProductInfos(@Param("monthDate") String monthDate);
}
