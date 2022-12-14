package com.pofa.ebcadmin.profitReport.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pofa.ebcadmin.profitReport.entity.MismatchedSkusInfo;
import com.pofa.ebcadmin.profitReport.entity.ProfitReportInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface ProfitReportDao extends BaseMapper<ProfitReportInfo> {

    @Select("""
                        
            WITH product_ascription AS (
              SELECT
                a.product,
                department,
                team,
                owner
              FROM
                pofa.ascriptions a
                join (
                  SELECT
                    product,
                    max(start_time) as start_time
                  FROM
                    pofa.ascriptions
                  where
                    start_time <=${monthDate}
                  group by
                    product
                ) as b on a.product = b.product
                and a.start_time = b.start_time
            ),
            fake_order AS (
              select
                id,
                brokerage
              from
                z_fakeorders_purchased_${month}
              where
                order_payment_time =${monthDate}
            ),
            product_orders AS (
              select
                product_id,
                product_count,
                actual_amount,
                sku_name,
                brokerage
              from
                z_orders_${monthDate}
                LEFT JOIN fake_order on z_orders_${monthDate}.order_id = fake_order.id
                join product_ascription on z_orders_${monthDate}.product_id = product_ascription.product
            ),
            manufacturers_temp AS (
              SELECT
                a.product_id,
                freight,
                freight_to_payment,
                a.start_time,
                create_time
              FROM
                pofa.manufacturers a
                join (
                  SELECT
                    product_id,
                    max(start_time) as start_time
                  FROM
                    pofa.manufacturers
                  where
                    start_time <=${monthDate}
                  group by
                    product_id
                ) as b on a.product_id = b.product_id
                and a.start_time = b.start_time
            ),
            manufacturers AS (
              SELECT
                a.product_id,
                freight,
                freight_to_payment,
                a.start_time
              FROM
                manufacturers_temp a
                join (
                  SELECT
                    product_id,
                    max(create_time) as create_time
                  FROM
                    manufacturers_temp
                  group by
                    product_id
                ) as b on a.product_id = b.product_id
                and a.create_time = b.create_time
            ),
            first_category_temp AS (
              SELECT
                a.category_id,
                deduction,
                insurance,
                a.start_time,
                create_time
              FROM
                pofa.categoryhistorys a
                join (
                  SELECT
                    category_id,
                    max(start_time) as start_time
                  FROM
                    pofa.categoryhistorys
                  where
                    start_time <=${monthDate}
                  group by
                    category_id
                ) as b on a.category_id = b.category_id
                and a.start_time = b.start_time
            ),
            first_category AS (
              SELECT
                a.category_id,
                deduction,
                insurance,
                a.start_time
              FROM
                first_category_temp a
                join (
                  SELECT
                    category_id,
                    max(create_time) as create_time
                  FROM
                    first_category_temp
                  group by
                    category_id
                ) as b on a.category_id = b.category_id
                and a.create_time = b.create_time
            ),
            a AS (
              SELECT
                product_id,
                count(*) as order_count,
                sum(product_count) as product_count,
                sum(actual_amount) as total_amount
              FROM
                product_orders
              GROUP BY
                product_id
            ),
            sku_temp AS (
              SELECT
                a.sku_id,
                sku_name,
                sku_price,
                sku_cost,
                a.product_id,
                a.start_time,
                create_time
              FROM
                pofa.skus a
                join (
                  SELECT
                    sku_id,
                    max(start_time) as start_time
                  FROM
                    pofa.skus
                  where
                    start_time <=${monthDate}
                  group by
                    sku_id
                ) as b on a.sku_id = b.sku_id
                and a.start_time = b.start_time
            ),
            product_sku AS(
              SELECT
                a.sku_id,
                a.product_id,
                sku_price,
                sku_cost,
                sku_name,
                a.start_time
              FROM
                sku_temp a
                join (
                  SELECT
                    sku_id,
                    max(create_time) as create_time
                  FROM
                    sku_temp
                  group by
                    sku_id
                ) as b on a.sku_id = b.sku_id
                and a.create_time = b.create_time
            ),
            product_statistic AS (
              select
                product_id,
                sum(total_price) as total_price,
                sum(total_cost) as total_cost,
                sum(total_brokerage) as total_brokerage,
                sum(total_fake_amount) as total_fake_amount,
                sum(fake_count) as total_fake_count,
                count(*) - count(sku_price) as wrong_count
              from
                (
                  select
                    a.product_id,
                    a.sku_name,
                    product_count,
                    sku_price,
                    sku_cost,
                    product_count * sku_price as total_price,
                    (product_count - fake_count) * sku_cost as total_cost,
                    total_brokerage,
                    total_fake_amount,
                    fake_count
                  from
                    (
                      select
                        product_id,
                        sku_name,
                        sum(product_count) as product_count,
                        count(brokerage) as fake_count,
                        sum(brokerage) as total_brokerage,
                        sum(if (brokerage > 0, actual_amount, 0)) as total_fake_amount -- 判断是否刷单
                      from
                        product_orders
                      group by
                        product_id,
                        sku_name
                    ) as a
                    left join product_sku on a.sku_name = product_sku.sku_name
                    and a.product_id = product_sku.product_id
                ) as b
              group by
                product_id
            ),
            finished_refund_order_amount AS (
              SELECT
                product_id as refund_product_id,
                sum(refund_amount) as total_refund_amount,
                sum(if (express_status = false, refund_amount, 0)) as total_refund_with_no_ship_amount
              FROM
                (
                  SELECT
                    product_id,
                    refund_amount,
                    express_status
                  FROM
                    pofa.z_refundorders_finished_${month}
                    join product_ascription on z_refundorders_finished_${month}.product_id = product_ascription.product
                  where
                    refund_end_time = ${monthDate}
                    AND refund_status = 1
                ) as a
              group by
                product_id
            ),
            d AS (
              select
                order_id,
                actual_amount,
                product_id
              from
                z_orders_${monthDate}
            )
            SELECT
              i.product_id,
              shop_name,
              first_category,
              deduction,
              insurance,
              product_name,
              freight,
              freight_to_payment,
              product_ascription.department,
              product_ascription.team,
              product_ascription.owner,
              order_count,
              product_count,
              total_amount,
              total_refund_amount,
              total_refund_with_no_ship_amount,
              total_fake_count,
              total_fake_amount,
              total_brokerage,
              total_price,
              total_cost,
              wrong_count
            from
              (
                SELECT
                  ifnull(product_id, refund_product_id) as product_id,
                  order_count,
                  product_count,
                  total_amount,
                  total_refund_amount,
                  total_refund_with_no_ship_amount
                from
                  (
                    SELECT
                      *
                    FROM
                      a
                      LEFT JOIN finished_refund_order_amount ON a.product_id = finished_refund_order_amount.refund_product_id
                    UNION
                    SELECT
                      *
                    FROM
                      a
                      RIGHT JOIN finished_refund_order_amount ON a.product_id = finished_refund_order_amount.refund_product_id
                  ) as z
              ) as i
              join product_ascription on i.product_id = product_ascription.product
              join pofa.products on i.product_id = pofa.products.id
              left join manufacturers on i.product_id = manufacturers.product_id
              left join first_category on pofa.products.first_category = first_category.category_id
              left join product_statistic on i.product_id = product_statistic.product_id;
                        
            """)
    List<ProfitReportInfo> calculateDailyReport(@Param("month") String month, @Param("monthDate") String monthDate);


    @Select("""
                     WITH product_orders AS (
                         select
                             product_id,
                             product_count,
                             actual_amount,
                             sku_name
                         from
                             z_orders_${monthDate}
                         where
                             product_id = ${productId}
                     ),
                     sku_temp AS (
                         SELECT
                             a.sku_id,
                             sku_name,
                             a.start_time,
                             create_time
                         FROM
                             pofa.skus a
                             join (
                                 SELECT
                                     sku_id,
                                     max(start_time) as start_time
                                 FROM
                                     pofa.skus
                                 where
                                     product_id = ${productId} and
                                     start_time <= ${monthDate}
                                 group by
                                     sku_id
                             ) as b on a.sku_id = b.sku_id
                             and a.start_time = b.start_time
                     ),
                     product_sku AS(
                         SELECT
                             sku_name
                         FROM
                             sku_temp a
                             join (
                                 SELECT
                                     sku_id,
                                     max(create_time) as create_time
                                 FROM
                                     sku_temp
                                 group by
                                     sku_id
                             ) as b on a.sku_id = b.sku_id
                             and a.create_time = b.create_time
                     )
                     select
                         a.sku_name,
                         product_count,
                         total_amount
                     from
                         (
                             select
                                 sku_name,
                                 sum(product_count) as product_count,
                                 sum(actual_amount) as total_amount
                             from
                                 product_orders
                             group by
                                 sku_name
                         ) as a
                         left join product_sku on a.sku_name = product_sku.sku_name where product_sku.sku_name is NULL;
            """)
    List<MismatchedSkusInfo> getMismatchedSkus(@Param("month") String month, @Param("monthDate") String monthDate, @Param("productId") Long productId);


}