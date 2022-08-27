package com.pofa.ebcadmin.product.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pofa.ebcadmin.mybatisplus.CustomTableNameHandler;
import com.pofa.ebcadmin.order.dao.OrderDao;
import com.pofa.ebcadmin.order.entity.FakeOrderInfo;
import com.pofa.ebcadmin.order.entity.OrderInfo;
import com.pofa.ebcadmin.order.entity.RefundOrderInfo;
import com.pofa.ebcadmin.product.dao.AscriptionDao;
import com.pofa.ebcadmin.product.dao.MismatchProductDao;
import com.pofa.ebcadmin.product.dao.ProductDao;
import com.pofa.ebcadmin.product.dto.Product;
import com.pofa.ebcadmin.product.entity.AscriptionInfo;
import com.pofa.ebcadmin.product.entity.MismatchProductInfo;
import com.pofa.ebcadmin.product.entity.ProductInfo;
import com.pofa.ebcadmin.product.entity.SkuInfo;
import com.pofa.ebcadmin.product.service.AscriptionService;
import com.pofa.ebcadmin.product.service.ProductService;
import com.pofa.ebcadmin.utils.Convert;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    public OrderDao orderDao;

    @Autowired
    public OrderInfo orderInfo;

    @Autowired
    public ProductDao productDao;

    @Autowired
    public AscriptionDao ascriptionDao;

    @Autowired
    public ProductInfo productInfo;

    @Autowired
    public AscriptionInfo ascriptionInfo;


    @Autowired
    public MismatchProductDao mismatchProductDao;


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int addProduct(Product.AddDTO dto) {
        if (dto.getId() > 10000000000000000L) return -101;
        var wrapper = new QueryWrapper<ProductInfo>().eq("id", dto.getId());
        var productInfos = productDao.selectList(wrapper);
        if (productInfos.isEmpty()) {
            //新建商品
            var date = new Date();
            date.setTime(0);
            productDao.insert(productInfo
                    .setId(dto.getId())
                    .setDepartment(dto.getDepartment())
                    .setTeam(dto.getTeam())
                    .setOwner(dto.getOwner())
                    .setShopName(dto.getShopName())
                    .setProductName(dto.getProductName())
                    .setFirstCategory(dto.getFirstCategory())
                    .setTransportWay(dto.getTransportWay())
                    .setStorehouse(dto.getStorehouse())
                    .setNote(dto.getNote()));
            //
            ascriptionDao.insert(ascriptionInfo
                    .setProduct(dto.getId())
                    .setDepartment(dto.getDepartment())
                    .setTeam(dto.getTeam())
                    .setOwner(dto.getOwner())
                    .setStartTime(date)
                    .setNote("初始归属"));

            _tryMatchMisMatchProduct(dto.getId());
            return 1;
        }
        return -100;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int editProduct(Product.EditDTO dto) {
        if (dto.getStartTime() != null) {
            var list = ascriptionDao.selectList(
                    new QueryWrapper<AscriptionInfo>()
                            .select("department", "team", "owner", "start_time")
                            .eq("product", dto.getId())
                            .orderByDesc("start_time")
                            .last("limit 1")
            );

            if (list.size() == 1){
                if (list.get(0).getStartTime().getTime() == dto.getStartTime().getTime()){
                    return -1;
                }
            }

            ascriptionDao.insert(ascriptionInfo
                    .setProduct(dto.getId())
                    .setDepartment(dto.getDepartment())
                    .setTeam(dto.getTeam())
                    .setOwner(dto.getOwner())
                    .setStartTime(dto.getStartTime())
                    .setNote(""));

            list = ascriptionDao.selectList(
                    new QueryWrapper<AscriptionInfo>()
                            .select("department", "team", "owner")
                            .eq("product", dto.getId())
                            .orderByDesc("start_time")
                            .last("limit 1")
            );

            return productDao.update(productInfo
                            .setDepartment(list.get(0).getDepartment())
                            .setTeam(list.get(0).getTeam())
                            .setOwner(list.get(0).getOwner())
                            .setShopName(dto.getShopName())
                            .setProductName(dto.getProductName())
                            .setFirstCategory(dto.getFirstCategory())
                            .setTransportWay(dto.getTransportWay())
                            .setStorehouse(dto.getStorehouse())
                            .setNote(dto.getNote()),
                    new UpdateWrapper<ProductInfo>().eq("id", dto.getId()));
        } else {
            return productDao.update(new ProductInfo()
                            .setShopName(dto.getShopName())
                            .setProductName(dto.getProductName())
                            .setFirstCategory(dto.getFirstCategory())
                            .setTransportWay(dto.getTransportWay())
                            .setStorehouse(dto.getStorehouse())
                            .setNote(dto.getNote()),
                    new UpdateWrapper<ProductInfo>().eq("id", dto.getId()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public JSONObject getProductsByUserIds(List<Long> users, Product.GetDTO dto) {

        //json格式的匹配规则：select类别匹配，search模糊匹配
        var match = JSON.parseObject(dto.getMatch(), JSONObject.class);
        var select = match.getJSONObject("select");
        var search = match.getJSONObject("search");
        log.info(String.valueOf(select));
        log.info(String.valueOf(search));
        log.info("------------------");

        //sql待优化，暂时不需要
        var wrapper = new QueryWrapper<ProductInfo>().in("owner", users).eq("deprecated", false);

        //类别删选
        for (Map.Entry<String, Object> entry : select.entrySet()) {
            var value = (JSONArray) (entry.getValue());
            if (value.isEmpty()) continue;
            var items = new ArrayList<String>();
            value.forEach(item -> items.add((String) item));
            wrapper.in(Convert.camelToUnderScore(entry.getKey()), items);
        }

        //模糊查找
        for (Map.Entry<String, Object> entry : search.entrySet()) {
            var value = (String) (entry.getValue());
            if (value.isEmpty()) continue;

            wrapper.like(Convert.camelToUnderScore(entry.getKey()), value);
        }


        var page = new Page<ProductInfo>(dto.getPage(), dto.getItemsPerPage());
        productDao.selectPage(page, wrapper);
        return new JSONObject().fluentPut("products", page.getRecords()).fluentPut("total", page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public JSONObject getCategorysByUserIds(List<Long> users) {
        var data = new JSONObject();

        //有提升空间，暂时不需要

        var targets = new ArrayList<String>();
        targets.add("department");
        targets.add("team");
        targets.add("owner");
        targets.add("shop_name");
        targets.add("first_category");
        targets.add("transport_way");

        List<ProductInfo> results;
        for (var col : targets) {
            var array = new JSONArray();

//            var wrapper = new QueryWrapper<ProductInfo>().select(col).groupBy(col).and(i -> {
//                for (Long id : users) i.eq("owner", id).or();
//            });

            var wrapper = new QueryWrapper<ProductInfo>().select(col).groupBy(col).in("owner", users);

            results = productDao.selectList(wrapper);
            results.forEach(item -> array.add(switch (col) {
                case "department" -> item.getDepartment();
                case "team" -> item.getTeam();
                case "owner" -> item.getOwner();
                case "shop_name" -> item.getShopName();
                case "first_category" -> item.getFirstCategory();
                case "transport_way" -> item.getTransportWay();
                default -> "ERROR";
            }));
            data.put(Convert.underScoreToCamel(col), array);
        }

        log.info(String.valueOf(data));
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int deprecateProductById(Long id) {
        return productDao.update(null, new UpdateWrapper<ProductInfo>().in("id", id).set("deprecated", true));
    }

    public void _tryMatchMisMatchProduct(Long productId) {
        mismatchProductDao.delete(new QueryWrapper<MismatchProductInfo>().eq("id", productId));
    }

    public List<MismatchProductInfo> getMismatchProducts() {
        var mismatchProducts = mismatchProductDao.selectList(null);
        if (mismatchProducts.isEmpty()){
            return mismatchProducts;
        }

        mismatchProducts.forEach(mismatchProduct -> mismatchProduct.setTotalAmount(BigDecimal.valueOf(0)));

        var mismatchProductMap = mismatchProducts.stream().collect(Collectors.toMap(MismatchProductInfo::getId, info -> info));
        var mismatchProductIds = mismatchProducts.stream().map(MismatchProductInfo::getId).toList();

        var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        for (int j = 0; j < 30; j++) { //从全部订单里追溯30天
            System.out.println("追溯-" + dayFormat.format(calendar.getTime()));
            CustomTableNameHandler.customTableName.set("z_orders_" + dayFormat.format(calendar.getTime()));
            var result = orderDao.selectList(new QueryWrapper<OrderInfo>().select("product_id", "sum(actual_amount) as product_total_amount").in("product_id", mismatchProductIds).groupBy("product_id"));
            result.forEach(orderInfo -> {
                var product = mismatchProductMap.get(orderInfo.getProductId());
                product.setTotalAmount(product.getTotalAmount().add(orderInfo.getProductTotalAmount()));
            });
            calendar.add(Calendar.DATE, -1);
        }
        return mismatchProductMap.values().stream().toList();
    }

}
