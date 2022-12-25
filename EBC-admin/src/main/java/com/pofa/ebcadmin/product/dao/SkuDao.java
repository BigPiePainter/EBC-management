package com.pofa.ebcadmin.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pofa.ebcadmin.product.entity.SkuInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

@Mapper
public interface SkuDao extends BaseMapper<SkuInfo> {
    Integer insertBatchSomeColumn(Collection<SkuInfo> entityList);


    @Delete("""
            WITH sku_temp as (
                select
                    product_id,
                    sku_id,
                    sku_name,
                    start_time,
                    max(create_time) as max
                from
                    pofa.skus
                where
                    product_id = ${productId}
                group by
                    product_id,
                    sku_id,
                    sku_name,
                    start_time
                having
                    count(*) > 1
            )
            DELETE from
                pofa.skus
            where
                uid in(
                    SELECT
                        uid
                    from
                        (
                            select
                                uid
                            from
                                pofa.skus
                                right join sku_temp on pofa.skus.product_id = sku_temp.product_id
                                and pofa.skus.sku_id = sku_temp.sku_id
                                and pofa.skus.sku_name = sku_temp.sku_name
                                and pofa.skus.start_time = sku_temp.start_time
                                and pofa.skus.create_time != sku_temp.max
                        ) as a
                )
            """)
    Integer deleteUnusedSkuInfos(@Param("productId") Long productId);
}
