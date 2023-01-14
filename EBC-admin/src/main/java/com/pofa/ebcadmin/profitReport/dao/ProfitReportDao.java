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
                    start_time <= ${monthDate}
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
                order_payment_time = ${monthDate}
            ),
            fake_order_personal_purchased AS (
              select
                id
              from
                z_fakeorders_personal_purchased_${month}
              where
                order_payment_time = ${monthDate}
            ),
            product_orders AS (
              select
                product_id,
                product_count,
                sku_name,
                actual_amount,
                brokerage,
                fake_order_personal_purchased.id as personal
              from
                z_orders_${monthDate}
                LEFT JOIN fake_order on z_orders_${monthDate}.order_id = fake_order.id
                LEFT JOIN fake_order_personal_purchased on z_orders_${monthDate}.order_id = fake_order_personal_purchased.id -- join product_ascription on z_orders_${monthDate}.product_id = product_ascription.product
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
            a AS (
              SELECT
                product_id,
                count(*) as order_count,
                sum(product_count) as product_count
              FROM
                product_orders
              GROUP BY
                product_id
            ),
            product_sku AS (
              SELECT
                *
              from
                (
                  SELECT
                    product_id,
                    sku_name,
                    sku_price,
                    sku_cost,
                    start_time,
                    create_time,
                    ROW_NUMBER() OVER (
                      PARTITION BY product_id,
                      sku_name
                      ORDER BY
                        start_time DESC
                    ) AS num
                  FROM
                    skus
                  where
                    start_time <= ${monthDate}
                ) a
              where
                num = 1
            ),
            product_statistic AS (
              select
                product_id,
                sum(actual_amount) as total_amount,
                sum(total_price) as total_price,
                sum(total_cost) as total_cost,
                sum(total_brokerage) as total_brokerage,
                sum(fake_amount) as total_fake_amount,
                sum(fake_count) as total_fake_count,
                sum(personal_fake_count) as total_personal_fake_count,
                sum(personal_fake_amount) as total_personal_fake_amount,
                sum(personal_fake_enabling_count) as total_personal_fake_enabling_count,
                sum(personal_fake_enabling_amount) as total_personal_fake_enabling_amount,
                count(*) - count(sku_cost) as wrong_count
              from
                (
                  select
                    a.product_id,
                    a.sku_name,
                    product_count,
                    actual_amount,
                    sku_cost,
                    if (
                      sku_price is null,
                      actual_amount,
                      product_count * sku_price
                    ) as total_price,
                    if (
                      sku_price is null,
                      actual_amount / product_count * (
                        product_count - fake_product_count - personal_fake_enabling_product_count - personal_fake_product_count
                      ),
                      (
                        product_count - fake_product_count - personal_fake_enabling_product_count - personal_fake_product_count
                      ) * sku_cost
                    ) as total_cost,
                    total_brokerage,
                    fake_count,
                    -- 团队刷单数量
                    fake_amount,
                    -- 团队刷单额
                    personal_fake_count,
                    -- 个人刷单数量
                    personal_fake_amount,
                    -- 个人刷单额
                    personal_fake_enabling_count,
                    -- 个人破零数
                    personal_fake_enabling_amount -- 个人破零额
                  from
                    (
                      select
                        product_id,
                        sku_name,
                        sum(product_count) as product_count,
                        sum(actual_amount) as actual_amount,
                        count(
                          brokerage > 0
                          or null
                        ) as fake_count,
                        -- 团队刷单数量
                        sum(if (brokerage > 0, product_count, 0)) as fake_product_count,
                        -- 团队刷单品数
                        sum(
                          if (
                            brokerage > 0,
                            actual_amount,
                            0
                          )
                        ) as fake_amount,
                        -- 团队刷单额
                        count(
                          (brokerage = 0)
                          or null
                        ) as personal_fake_enabling_count,
                        -- 个人破零数量
                        sum(
                          if (
                            brokerage = 0,
                            product_count,
                            0
                          )
                        ) as personal_fake_enabling_product_count,
                        -- 个人破零品数
                        sum(
                          if (
                            brokerage = 0,
                            actual_amount,
                            0
                          )
                        ) as personal_fake_enabling_amount,
                        -- 个人破零额
                        count(
                          (personal)
                          or null
                        ) as personal_fake_count,
                        -- 个人刷单数量
                        sum(
                          if (
                            personal,
                            product_count,
                            0
                          )
                        ) as personal_fake_product_count,
                        -- 个人刷单品数
                        sum(
                          if (
                            personal,
                            actual_amount,
                            0
                          )
                        ) as personal_fake_amount,
                        -- 个人刷单额
                        sum(brokerage) as total_brokerage -- 总刷单佣金
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
                sum(if (express_status = false, refund_amount, 0)) as total_refund_with_no_ship_amount,
                count(
                  express_status = false
                  or null
                ) as total_refund_with_no_ship_count
              FROM
                (
                  SELECT
                    product_id,
                    refund_amount,
                    express_status
                  FROM
                    pofa.z_refundorders_finished_${month} -- join product_ascription on z_refundorders_finished_${month}.product_id = product_ascription.product
                  where
                    refund_end_time = ${monthDate}
                    AND refund_status = 1
                    AND order_id NOT IN (
                      select
                        id
                      from
                        z_fakeorders_personal_finished_${month}
                    )
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
              ifnull(order_count, 0) as order_count,
              ifnull(product_count, 0) as product_count,
              ifnull(total_amount, 0) as total_amount,
              ifnull(total_refund_amount, 0) as total_refund_amount,
              ifnull(total_refund_with_no_ship_amount, 0) as total_refund_with_no_ship_amount,
              ifnull(total_refund_with_no_ship_count, 0) as total_refund_with_no_ship_count,
              ifnull(total_fake_count, 0) as total_fake_count,
              ifnull(total_fake_amount, 0) as total_fake_amount,
              ifnull(total_personal_fake_count, 0) as total_personal_fake_count,
              ifnull(total_personal_fake_amount, 0) as total_personal_fake_amount,
              ifnull(total_personal_fake_enabling_count, 0) as total_personal_fake_enabling_count,
              ifnull(total_personal_fake_enabling_amount, 0) as total_personal_fake_enabling_amount,
              ifnull(total_brokerage, 0) as total_brokerage,
              ifnull(total_price, 0) as total_price,
              ifnull(total_cost, 0) as total_cost,
              ifnull(wrong_count, 0) as wrong_count -- ROW_NUMBER() OVER (
              --   PARTITION BY department,
              --   team,
              --   owner
              -- ) AS num
            from
              (
                SELECT
                  ifnull(product_id, refund_product_id) as product_id,
                  order_count,
                  product_count,
                  total_refund_amount,
                  total_refund_with_no_ship_amount,
                  total_refund_with_no_ship_count
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
              left join product_statistic on i.product_id = product_statistic.product_id
            """)
    List<ProfitReportInfo> calculateDailyReport(@Param("month") String month, @Param("monthDate") String monthDate);


    @Select("""
                     WITH fake_order AS (
                         select
                             id,
                             brokerage
                         from
                             z_fakeorders_purchased_${month}
                         where
                             order_payment_time = ${monthDate}
                     ),
                     fake_order_personal_purchased AS (
                         select
                             id
                         from
                             z_fakeorders_personal_purchased_${month}
                         where
                             order_payment_time = ${monthDate}
                     ),
                     product_orders AS (
                         select
                             product_id,
                             product_count,
                             sku_name,
                             actual_amount,
                             brokerage,
                             fake_order_personal_purchased.id as personal
                         from
                             z_orders_${monthDate}
                             LEFT JOIN fake_order on z_orders_${monthDate}.order_id = fake_order.id
                             LEFT JOIN fake_order_personal_purchased on z_orders_${monthDate}.order_id = fake_order_personal_purchased.id -- join product_ascription on z_orders_${monthDate}.product_id = product_ascription.product
                         where
                             product_id = #{productId}
                     ),
                     product_sku AS (
                         SELECT
                             *
                         from
                             (
                                 SELECT
                                     product_id,
                                     sku_name,
                                     sku_price,
                                     sku_cost,
                                     start_time,
                                     create_time,
                                     ROW_NUMBER() OVER (
                                         PARTITION BY product_id,
                                         sku_name
                                         ORDER BY
                                             start_time DESC
                                     ) AS num
                                 FROM
                                     skus
                                 where
                                     start_time <= ${monthDate}
                                     and product_id = #{productId}
                             ) a
                         where
                             num = 1
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
                         left join product_sku on a.sku_name = product_sku.sku_name
                     where
                         product_sku.sku_name is NULL
            """)
    List<MismatchedSkusInfo> getMismatchedSkus(@Param("month") String month, @Param("monthDate") String monthDate, @Param("productId") Long productId);


}