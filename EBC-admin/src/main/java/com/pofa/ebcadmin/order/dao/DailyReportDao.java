package com.pofa.ebcadmin.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pofa.ebcadmin.order.entity.DailyReportInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface DailyReportDao extends BaseMapper<DailyReportInfo> {

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
                    start_time <= ${monthDate}
                  group by
                    product
                ) as b on a.product = b.product
                and a.start_time = b.start_time
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
                    start_time <= ${monthDate}
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
                    start_time <= ${monthDate}
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
            product_orders AS (
              select
                z_orders_${monthDate}.product_id,
                product_count,
                actual_amount,
                sku_name
              from
                product_ascription
                join z_orders_${monthDate} on z_orders_${monthDate}.product_id = product_ascription.product
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
            skus_temp AS (
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
                    start_time <= ${monthDate}
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
                skus_temp a
                join (
                  SELECT
                    sku_id,
                    max(create_time) as create_time
                  FROM
                    skus_temp
                  group by
                    sku_id
                ) as b on a.sku_id = b.sku_id
                and a.create_time = b.create_time
            ),
            sku_info AS (
              select
                product_id,
                sum(total_price) as total_price,
                sum(total_cost) as total_cost,
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
                    product_count * sku_cost as total_cost
                  from
                    (
                      select
                        product_id,
                        sku_name,
                        sum(product_count) as product_count
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
            refund_order AS (
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
            ),
            b AS (
              select
                total_refund_amount.product_id as refund_product_id,
                total_refund_amount,
                total_refund_with_no_ship_amount
              from
                (
                  SELECT
                    product_id,
                    sum(refund_amount) as total_refund_amount
                  FROM
                    refund_order
                  group by
                    product_id
                ) as total_refund_amount
                left join (
                  SELECT
                    product_id,
                    sum(refund_amount) as total_refund_with_no_ship_amount
                  FROM
                    refund_order
                  where
                    express_status = false
                  group by
                    product_id
                ) as refund_with_no_ship on total_refund_amount.product_id = refund_with_no_ship.product_id
            ),
            c AS (
              select
                id,
                brokerage
              from
                z_fakeorders_purchased_${month}
              where
                order_payment_time = ${monthDate}
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
              fake_order_count,
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
                  total_refund_with_no_ship_amount,
                  fake_order_count,
                  total_fake_amount,
                  total_brokerage
                from
                  (
                    SELECT
                      *
                    FROM
                      a
                      LEFT JOIN b ON a.product_id = b.refund_product_id
                    UNION
                    SELECT
                      *
                    FROM
                      a
                      RIGHT JOIN b ON a.product_id = b.refund_product_id
                  ) as z
                  left join (
                    select
                      product_id as fake_order_product_id,
                      count(*) as fake_order_count,
                      sum(actual_amount) as total_fake_amount,
                      sum(brokerage) as total_brokerage
                    from
                      c
                      left join d on c.id = d.order_id
                    group by
                      fake_order_product_id
                  ) as h on z.product_id = h.fake_order_product_id
              ) as i
              join product_ascription on i.product_id = product_ascription.product
              join pofa.products on i.product_id = pofa.products.id
              left join manufacturers on i.product_id = manufacturers.product_id
              left join first_category on pofa.products.first_category = first_category.category_id
              left join sku_info on i.product_id = sku_info.product_id;""")
    List<DailyReportInfo> calculateDailyReport(@Param("month") String month, @Param("monthDate") String monthDate);
}