package com.pofa.ebcadmin.order.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pofa.ebcadmin.department.entity.DepartmentInfo;
import com.pofa.ebcadmin.mybatisplus.CustomTableNameHandler;
import com.pofa.ebcadmin.order.dao.FakeOrderDao;
import com.pofa.ebcadmin.order.dao.OrderDao;
import com.pofa.ebcadmin.order.dao.PersonalFakeOrderDao;
import com.pofa.ebcadmin.order.dao.RefundOrderDao;
import com.pofa.ebcadmin.order.dto.Order;
import com.pofa.ebcadmin.order.entity.FakeOrderInfo;
import com.pofa.ebcadmin.order.entity.OrderInfo;
import com.pofa.ebcadmin.order.entity.PersonalFakeOrderInfo;
import com.pofa.ebcadmin.order.entity.RefundOrderInfo;
import com.pofa.ebcadmin.order.service.OrderService;
import com.pofa.ebcadmin.product.dao.MismatchProductDao;
import com.pofa.ebcadmin.product.dao.ProductDao;
import com.pofa.ebcadmin.product.dto.Product;
import com.pofa.ebcadmin.product.entity.MismatchProductInfo;
import com.pofa.ebcadmin.product.entity.ProductInfo;
import com.pofa.ebcadmin.order.orderUtils.*;
import com.pofa.ebcadmin.product.entity.SkuInfo;
import com.pofa.ebcadmin.team.entity.TeamInfo;
import com.pofa.ebcadmin.user.entity.UserInfo;
import com.pofa.ebcadmin.utils.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.pofa.ebcadmin.order.orderUtils.OrderFileUtils.*;


@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private static final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
    private static final SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public OrderDao orderDao;

    @Autowired
    public OrderInfo orderInfo;

    @Autowired
    public ProductDao productDao;

    @Autowired
    public FakeOrderDao fakeOrderDao;

    @Autowired
    public PersonalFakeOrderDao personalFakeOrderDao;

    @Autowired
    public FakeOrderInfo fakeOrderInfo;

    @Autowired
    public RefundOrderDao refundOrderDao;


    @Autowired
    public MismatchProductDao mismatchProductDao;


    public String[] orderHeader = {"子订单编号", "主订单编号", "买家会员名", "买家会员ID", "支付单号", "买家应付货款", "买家应付邮费", "总金额", "买家实际支付金额", "子单实际支付金额", "订单状态", "收件人姓名", "收货地址", "联系手机", "订单创建时间", "订单付款时间", "宝贝标题", "宝贝数量", "物流单号", "物流公司", "店铺ID", "店铺名称", "供应商ID", "供应商名称", "仓发类型", "退款金额", "颜色/尺码", "商家编码", "商品编码"};
    public String[] refundOrderHeader = {"订单编号", "退款单编号", "退款类型", "订单付款时间", "商品ID", "订单创建时间", "宝贝标题", "交易金额", "买家退款金额", "发货状态", "发货物流信息", "是否需要退货", "退款申请时间", "退款状态", "客服介入状态", "卖家退货地址", "退货物流单号", "退货物流公司", "买家退款原因", "完结时间", "退款操作人", "卖家备注"};
    public String[] fakeOrderHeader = {"序号", "会员号", "订单编号", "价格", "价格合计", "佣金", "佣金合计", "诉求日期", "佣金", "品数", "本单佣金", "团队"};
    public String[] personalFakeOrderHeader = {"线上订单号", "付款日期"};


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public JSONObject getOrdersByDate(Order.GetOrderDTO dto) {

        //json格式的匹配规则：select类别匹配，search模糊匹配
        var match = JSON.parseObject(dto.getMatch(), JSONObject.class);
        var select = match.getJSONObject("select");
        var search = match.getJSONObject("search");
        log.info(String.valueOf(select));
        log.info(String.valueOf(search));
        log.info("------------------");

        var wrapper = new QueryWrapper<OrderInfo>();

        var wrapperBase = new QueryWrapper<OrderInfo>();


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
            if (entry.getValue() instanceof JSONArray values) {
                if (values.isEmpty()) continue;
                wrapper.in(Convert.camelToUnderScore(entry.getKey()), values.stream().toList());
            } else if (entry.getValue() instanceof String value) {
                if (value.isEmpty()) continue;
                wrapper.like(Convert.camelToUnderScore(entry.getKey()), value);
            }
        }


        var category = new JSONObject();

        var targets = new ArrayList<String>();
        targets.add("order_status");
        targets.add("shop_name");
        targets.add("storehouse_type");

        List<OrderInfo> results;
        for (var col : targets) {
            var array = new JSONArray();
            var _wrapper = wrapperBase.clone();
            CustomTableNameHandler.customTableName.set("z_orders_" + dayFormat.format(dto.getDate()));
            results = orderDao.selectList(_wrapper.select(col).groupBy(col));
            results.forEach(item -> array.add(switch (col) {
                case "order_status" -> item.getOrderStatus();
                case "shop_name" -> item.getShopName();
                case "storehouse_type" -> item.getStorehouseType();
                default -> "ERROR";
            }));
            category.put(Convert.underScoreToCamel(col), array);
        }

        log.info(String.valueOf(category));

        CustomTableNameHandler.customTableName.set("z_orders_" + dayFormat.format(dto.getDate()));
        var total = orderDao.selectCount(wrapper);
        wrapper.last("limit " + (dto.getPage() - 1) * dto.getItemsPerPage() + ", " + dto.getItemsPerPage());
        var productInfoList = orderDao.selectList(wrapper);
        return new JSONObject().fluentPut("orders", productInfoList).fluentPut("total", total).fluentPut("category", category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public BigDecimal getRealTotalAmount(Date date) {
        CustomTableNameHandler.customTableName.set("z_orders_" + dayFormat.format(date));
        var result = orderDao.selectList(new QueryWrapper<OrderInfo>().select("SUM(actual_amount) as total_amount"));
        System.out.println(result);
        return result.size() == 0 ? BigDecimal.valueOf(0) : result.get(0).getTotalAmount();
    }



    @Override
    public void fileProcess(MultipartFile file) {
        var state = new FileState().setFileName(file.getOriginalFilename()).setSize(file.getSize()).setState("排队中");
        FileStateManager.newFile(state.getFileName(), state);

        try (InputStream inputStream = file.getInputStream()) {
            _fileProcess(state, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Excel 文件打开失败");
        }
    }


    public synchronized void _fileProcess(FileState state, InputStream inputStream) {
        log.info(state.getFileName() + " 开始处理");

        state.setCode(1); //processing
        state.setState("文件打开中");
        UploadWebSocket.sendStateToAll(state, true);

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            log.info("打开成功");
            var sheet = workbook.getSheetAt(0);

            //识别上传的订单类型
            //订单信息
            if (isValidFileType(sheet, orderHeader)) {
                if (orderPreProcess(sheet, state)) {
                    //调试，生成excel
//                    try (var outputStream = FileUtils.openOutputStream(new File("test.xlsx"));) {
//                        workbook.write(outputStream);
//                        log.info("写入成功");
//                    }
                    _orderFileProcess(sheet, state);
                }

                return;
            }
            //退单信息
            if (isValidFileType(sheet, refundOrderHeader)) {
                if (refundOrderPreProcess(sheet, state)) {
                    _refundOrderFileProcess(sheet, state);
                }
                return;
            }
            //刷单信息(团队刷单，破0刷单)
            if (isValidFileType(sheet, fakeOrderHeader)) {
                if (fakeOrderPreProcess(sheet, state)) {
                    _fakeOrderFileProcess(sheet, state);
                }
                return;
            }
            //个人刷单退款信息
            if (isValidFileType(sheet, personalFakeOrderHeader)) {
                if (personalFakeOrderPreProcess(sheet, state)) {
                    _personalFakeOrderFileProcess(sheet, state);
                }
                return;
            }

            state.setCode(-1); //error
            state.setState("没有匹配到任何文件类型，确保表格的第一行为数据头部");
            UploadWebSocket.sendStateToAll(state, true);
        } catch (Exception e) {
            e.printStackTrace();
            state.setCode(-1); //error
            state.setState("Excel 文件打开失败");
            UploadWebSocket.sendStateToAll(state, true);
            log.info("Excel 文件打开失败");
        }
        //log.info(state.getFileName() + " 处理完毕");
    }


    public void _orderFileProcess(Sheet sheet, FileState state) {
        state.setCode(1); //processing
        state.setState("订单 解析中");
        UploadWebSocket.sendStateToAll(state, true);
        log.info("订单 解析中");
        var lastRowNum = sheet.getLastRowNum();
        log.info(String.valueOf(lastRowNum));

        var orderInfos = new HashMap<String, ArrayList<OrderInfo>>();
        var mismatchProductInfos = new ArrayList<MismatchProductInfo>();

        int processingRow = 0;
        try {
            Row row;
            for (processingRow = 1; processingRow <= lastRowNum; processingRow++) {
                row = sheet.getRow(processingRow);
                if (isBlankRow(row, 21)) {
                    continue;
                }

                var id = Long.valueOf(row.getCell(0).getStringCellValue());
                var orderId = Long.valueOf(row.getCell(1).getStringCellValue());
                var paymentId = row.getCell(4).getStringCellValue();
                var amount = Double.parseDouble(row.getCell(5).getStringCellValue());
                var postage = Double.parseDouble(row.getCell(6).getStringCellValue());
                var totalAmount = Double.parseDouble(row.getCell(7).getStringCellValue());
                var actualTotalAmount = Double.parseDouble(row.getCell(8).getStringCellValue());
                var actualAmount = Double.parseDouble(row.getCell(9).getStringCellValue());
                var orderStatus = switch (row.getCell(10).getStringCellValue()) {
                    case "部分发货中" -> 1;
                    case "待买家收货" -> 2;
                    case "待卖家发货" -> 3;
                    case "交易成功" -> 4;
                    case "交易关闭" -> 5;
                    default -> 0;
                };
                var orderSetupTime = dateTimeFormat.parse(row.getCell(14).getStringCellValue());
                var orderPaymentTime = dateTimeFormat.parse(row.getCell(15).getStringCellValue());
                var productTitle = row.getCell(16).getStringCellValue();
                var productCount = Long.valueOf(row.getCell(17).getStringCellValue());
                var expressNumber = isBlankCell(row.getCell(18)) ? null : row.getCell(18).getStringCellValue();
                var expressCompany = isBlankCell(row.getCell(19)) ? null : row.getCell(19).getStringCellValue();
                var shopId = Long.valueOf(row.getCell(20).getStringCellValue());
                var shopName = row.getCell(21).getStringCellValue();
                var supplierId = Long.valueOf(row.getCell(22).getStringCellValue());
                var supplierName = row.getCell(23).getStringCellValue();
                var storehouseType = switch (row.getCell(24).getStringCellValue()) {
                    case "菜鸟仓" -> 1;
                    case "商家仓" -> 2;
                    default -> 0;
                };
                var refundAmount = Double.parseDouble(row.getCell(25).getStringCellValue().isBlank() ? "0" : row.getCell(25).getStringCellValue());
                var skuName = row.getCell(26).getStringCellValue();
                var sellerCode = isBlankCell(row.getCell(27)) ? null : row.getCell(27).getStringCellValue();
                var productId = Long.valueOf(row.getCell(28).getStringCellValue());


                var orderInfo = new OrderInfo()
                        .setId(id)
                        .setOrderId(orderId)
                        .setPaymentId(paymentId)
                        .setAmount(BigDecimal.valueOf(amount))
                        .setPostage(BigDecimal.valueOf(postage))
                        .setTotalAmount(BigDecimal.valueOf(totalAmount))
                        .setActualTotalAmount(BigDecimal.valueOf(actualTotalAmount))
                        .setActualAmount(BigDecimal.valueOf(actualAmount))
                        .setOrderStatus(orderStatus)
                        .setOrderSetupTime(orderSetupTime)
                        .setOrderPaymentTime(orderPaymentTime)
                        .setProductTitle(productTitle)
                        .setProductCount(productCount)
                        .setExpressNumber(expressNumber)
                        .setExpressCompany(expressCompany)
                        .setShopId(shopId)
                        .setShopName(shopName)
                        .setSupplierId(supplierId)
                        .setSupplierName(supplierName)
                        .setStorehouseType(storehouseType)
                        .setRefundAmount(BigDecimal.valueOf(refundAmount))
                        .setSkuName(skuName)
                        .setSellerCode(sellerCode)
                        .setProductId(productId);


                var belongPurchased = dayFormat.format(orderPaymentTime);

                if (!orderInfos.containsKey(belongPurchased)) {
                    orderInfos.put(belongPurchased, new ArrayList<>());
                }
                orderInfos.get(belongPurchased).add(orderInfo);
                mismatchProductInfos.add(new MismatchProductInfo().setId(productId).setProductTitle(productTitle));
            }

        } catch (Exception e) {
            e.printStackTrace();
            state.setCode(-1);
            state.setState(processingRow + 1 + "行 有系统未能识别的问题");
            return;
        }

        log.info("完事了");
        state.setState("订单 插入数据库");
        //插入 数据库
        try {
            _orderIntoDatabase(orderInfos, mismatchProductInfos, state);
        } catch (Exception e) {
            e.printStackTrace();
            state.setCode(-1);
            state.setState("订单 保存时发生了一些问题");
            return;
        }

        state.setCode(2); //成功
        state.setState(String.format("订单 完事了 插入%s行 忽略%s行", state.getRightRowNum(), state.getWrongRowNum()));
        UploadWebSocket.sendStateToAll(state, true);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public void _orderIntoDatabase(HashMap<String, ArrayList<OrderInfo>> orderInfos, ArrayList<MismatchProductInfo> mismatchProductInfos, FileState state) throws ParseException {
        log.info("1");
        for (var entry : orderInfos.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            var size = value.size();
            for (int i = 0; i < size; i += 10000) {
                CustomTableNameHandler.customTableName.set("z_orders_" + key);
                state.setState("订单 插入数据库 " + key);
                UploadWebSocket.sendStateToAll(state, true);
                orderDao.replaceBatchSomeColumn(value.subList(i, Math.min(i + 10000, size)));
            }
        }

        var fakeOrderRequested = new HashMap<String, ArrayList<FakeOrderInfo>>();

        //处理fakeOrder
        CustomTableNameHandler.customTableName.set("fakeorders");
        var fakeOrderInfos = fakeOrderDao.selectList(null);
        fakeOrderInfos.forEach(fakeOrder -> {
            var belongRequest = dayFormat.format(fakeOrder.getRequestTime());
            if (!fakeOrderRequested.containsKey(belongRequest)) {
                fakeOrderRequested.put(belongRequest, new ArrayList<>());
            }
            fakeOrderRequested.get(belongRequest).add(fakeOrder);
        });

        if (!fakeOrderRequested.isEmpty()) {
            _fakeOrderIntoDatabaseImpl(fakeOrderRequested);
        }

        //认领大厅
        mismatchProductDao.replaceBatchSomeColumn(mismatchProductInfos);
        _tryMatchMisMatchAllProducts();
    }

    public void _refundOrderFileProcess(Sheet sheet, FileState state) {
        state.setCode(1); //processing
        state.setState("退单 解析中");
        log.info("退单 解析中");
        var realRowNum = state.getRealRowNum();
        log.info(String.valueOf(realRowNum));

        var refundOrderPurchased = new HashMap<String, ArrayList<RefundOrderInfo>>();
        var refundOrderFinished = new HashMap<String, ArrayList<RefundOrderInfo>>();
        var refundOrderProcessing = new ArrayList<RefundOrderInfo>();
        var mismatchProductInfos = new ArrayList<MismatchProductInfo>();

        int processingRow = 0;
        try {
            Row row;
            for (processingRow = 1; processingRow <= realRowNum; processingRow++) {
                row = sheet.getRow(processingRow);

                var id = Long.valueOf(row.getCell(1).getStringCellValue()); //退款单编号;
                var orderId = Long.valueOf(row.getCell(0).getStringCellValue()); //订单编号
                var orderPaymentTime = dateTimeFormat.parse(row.getCell(3).getStringCellValue());

                var orderSetupTime = dateTimeFormat.parse(row.getCell(5).getStringCellValue()); //订单创建时间;
                var orderAmount = row.getCell(7).getNumericCellValue(); //订单金额
                var refundType = switch (row.getCell(2).getStringCellValue()) {
                    case "仅退款" -> 1;
                    case "退货退款" -> 2;
                    default -> 0;
                };
                var refundAmount = row.getCell(8).getNumericCellValue();
                var refundSetupTime = dateTimeFormat.parse(row.getCell(12).getStringCellValue());
                var refundStatus = switch (row.getCell(13).getStringCellValue()) {
                    case "退款成功" -> 1;
                    case "待买家发货" -> 2;
                    case "等待商家收货" -> 3;
                    case "退款待处理" -> 4;
                    case "退款关闭" -> 5;
                    case "已拒绝退款" -> 6;
                    default -> 0;
                };
                var refundReason = row.getCell(18).getStringCellValue();
                var refundEndTime = isBlankCell(row.getCell(19)) ? null : dateTimeFormat.parse(row.getCell(19).getStringCellValue());
                var productId = Long.valueOf(row.getCell(4).getStringCellValue());
                var productTitle = row.getCell(6).getStringCellValue();
                var expressStatus = switch (row.getCell(9).getStringCellValue()) {
                    case "已发货" -> true;
                    case "未发货" -> false;
                    default -> null;
                };
                var needReturn = switch (row.getCell(11).getStringCellValue()) {
                    case "是" -> true;
                    case "否" -> false;
                    default -> null;
                };
                var operator = isBlankCell(row.getCell(20)) ? null : row.getCell(20).getStringCellValue();
                var sellerNote = isBlankCell(row.getCell(21)) ? null : row.getCell(21).getStringCellValue();

                var expressInfo = isBlankCell(row.getCell(10)) ? null : row.getCell(10).getStringCellValue();
                var customerServiceStatus = isBlankCell(row.getCell(14)) ? null : row.getCell(14).getStringCellValue();

                var expressNumber = isBlankCell(row.getCell(16)) ? null : row.getCell(16).getStringCellValue();
                var expressCompany = isBlankCell(row.getCell(17)) ? null : row.getCell(17).getStringCellValue();
                var sellerAddress = isBlankCell(row.getCell(15)) ? null : row.getCell(15).getStringCellValue();


                var refundOrderInfo = new RefundOrderInfo()
                        .setId(id).
                        setOrderId(orderId).
                        setOrderPaymentTime(orderPaymentTime).
                        setOrderSetupTime(orderSetupTime).
                        setOrderAmount(BigDecimal.valueOf(orderAmount)).
                        setRefundType(refundType).
                        setRefundAmount(BigDecimal.valueOf(refundAmount)).
                        setRefundSetupTime(refundSetupTime).
                        setRefundStatus(refundStatus).
                        setRefundReason(refundReason).
                        setRefundEndTime(refundEndTime).
                        setProductId(productId).
                        setProductTitle(productTitle).
                        setExpressStatus(expressStatus).
                        setExpressInfo(expressInfo).
                        setExpressNumber(expressNumber).
                        setExpressCompany(expressCompany).
                        setNeedReturn(needReturn).
                        setCustomerServiceStatus(customerServiceStatus).
                        setSellerAddress(sellerAddress).
                        setOperator(operator).
                        setSellerNote(sellerNote);


                var belongPurchased = monthFormat.format(orderPaymentTime);

                if (!refundOrderPurchased.containsKey(belongPurchased)) {
                    refundOrderPurchased.put(belongPurchased, new ArrayList<>());
                }
                refundOrderPurchased.get(belongPurchased).add(refundOrderInfo);

                if (null == refundEndTime) { //未完结
                    refundOrderProcessing.add(refundOrderInfo);
                } else { //完结
                    var belongFinished = monthFormat.format(refundEndTime);
                    if (!refundOrderFinished.containsKey(belongFinished)) {
                        refundOrderFinished.put(belongFinished, new ArrayList<>());
                    }
                    refundOrderFinished.get(belongFinished).add(refundOrderInfo);
                }

                mismatchProductInfos.add(new MismatchProductInfo().setId(productId).setProductTitle(productTitle));
            }

        } catch (Exception e) {
            e.printStackTrace();
            state.setCode(-1);
            state.setState(processingRow + 1 + "行 有系统未能识别的问题");
            return;
        }

        log.info(String.valueOf(refundOrderPurchased.size()));
        log.info(String.valueOf(refundOrderFinished.size()));
        log.info(String.valueOf(refundOrderProcessing.size()));
        log.info("完事了");

        state.setState("退单 插入数据库");
        //插入 数据库

        try {
            _refundOrderIntoDatabase(refundOrderPurchased, refundOrderFinished, refundOrderProcessing, mismatchProductInfos);
        } catch (Exception e) {
            e.printStackTrace();
            state.setCode(-1);
            state.setState("退单 保存时发生了一些问题，疑似出现了远古退单数据，系统只给2022年1月以后的退单预留了储存空间");
            return;
        }

        state.setCode(2); //成功
        state.setState(String.format("退单 完事了"));
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public void _refundOrderIntoDatabase(HashMap<String, ArrayList<RefundOrderInfo>> refundOrderPurchased, HashMap<String, ArrayList<RefundOrderInfo>> refundOrderFinished, ArrayList<RefundOrderInfo> refundOrderProcessing, ArrayList<MismatchProductInfo> mismatchProductInfos) {
        log.info("1");
        for (var entry : refundOrderPurchased.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            var size = value.size();
            for (int i = 0; i < size; i += 10000) {
                CustomTableNameHandler.customTableName.set("z_refundorders_purchased_" + key);
                System.out.println(refundOrderDao.replaceBatchSomeColumn(value.subList(i, Math.min(i + 10000, size))));
            }
        }
        log.info("2");
        for (var entry : refundOrderFinished.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            var size = value.size();
            for (int i = 0; i < size; i += 10000) {
                var subInfos = value.subList(i, Math.min(i + 10000, size));
                CustomTableNameHandler.customTableName.set("z_refundorders_finished_" + key);
                System.out.println(refundOrderDao.replaceBatchSomeColumn(subInfos));
                CustomTableNameHandler.customTableName.set("refundorders");
                System.out.println(refundOrderDao.delete(new QueryWrapper<RefundOrderInfo>().in("id", subInfos.stream().map(RefundOrderInfo::getId).collect(Collectors.toList()))));
            }
        }
        log.info("3");
        var size = refundOrderProcessing.size();
        for (int i = 0; i < size; i += 10000) {
            CustomTableNameHandler.customTableName.set("refundorders");
            System.out.println(refundOrderDao.replaceBatchSomeColumn(refundOrderProcessing.subList(i, Math.min(i + 10000, size))));
        }


        //认领大厅
        mismatchProductDao.replaceBatchSomeColumn(mismatchProductInfos);
        _tryMatchMisMatchAllProducts();

        //个人刷单退款
        _tryMatchMisMatchAllPersonalFakeOrders();
    }


    public void _fakeOrderFileProcess(Sheet sheet, FileState state) {
        state.setCode(1); //processing
        state.setState("解析中（补单）");

        var realRowNum = state.getRealRowNum();

        var fakeOrderRequested = new HashMap<String, ArrayList<FakeOrderInfo>>();

        Row row;
        for (var i = 1; i <= realRowNum; i++) {
            row = sheet.getRow(i);

            var orderId = Long.parseLong(row.getCell(2).getStringCellValue());
            var requestTime = row.getCell(7).getDateCellValue();
            var productCount = Double.valueOf(row.getCell(9).getNumericCellValue()).longValue();
            var brokerage = BigDecimal.valueOf(row.getCell(10).getNumericCellValue());
            var team = row.getCell(11).getStringCellValue();

            var fakeOrderInfo = new FakeOrderInfo()
                    .setId(orderId)
                    .setRequestTime(requestTime)
                    .setProductCount(productCount)
                    .setBrokerage(brokerage)
                    .setTeam(team);


            var belongRequest = dayFormat.format(requestTime);

            if (!fakeOrderRequested.containsKey(belongRequest)) {
                fakeOrderRequested.put(belongRequest, new ArrayList<>());
            }
            fakeOrderRequested.get(belongRequest).add(fakeOrderInfo);
        }


        try {
            _fakeOrderIntoDatabase(fakeOrderRequested);
        } catch (Exception e) {
            e.printStackTrace();
            state.setCode(-1);
            state.setState("补单 保存时发生了一些问题");
            return;
        }


        state.setCode(2); //成功
        state.setState(String.format("补单 完事了"));
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public void _fakeOrderIntoDatabase(HashMap<String, ArrayList<FakeOrderInfo>> fakeOrderRequested) throws ParseException {
        _fakeOrderIntoDatabaseImpl(fakeOrderRequested);
    }


    public void _fakeOrderIntoDatabaseImpl(HashMap<String, ArrayList<FakeOrderInfo>> fakeOrderRequested) throws ParseException {
        log.info("1");
        var calendar = new GregorianCalendar();

        for (var entry : fakeOrderRequested.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            var size = value.size();
            for (int i = 0; i < size; i += 10000) {
                var fakeOrderRequestedListFragment = value.subList(i, Math.min(i + 10000, size));
                var undetectedFakeOrderFragment = fakeOrderRequestedListFragment.stream().collect(Collectors.toMap(FakeOrderInfo::getId, order -> order));
                var detectedFakeOrderFragment = new HashMap<Long, FakeOrderInfo>();
                var detectedFakeOrderPurchasedDateMapFragment = new HashMap<String, ArrayList<FakeOrderInfo>>();

                log.info("开始追溯");

                calendar.setTime(dayFormat.parse(key));
                for (int j = 0; j < 30; j++) { //从全部订单里追溯30天
                    log.info("剩余未检测：" + undetectedFakeOrderFragment.size());
                    if (undetectedFakeOrderFragment.isEmpty()) break;
                    CustomTableNameHandler.customTableName.set("z_orders_" + dayFormat.format(calendar.getTime()));
                    var result = orderDao.selectList(new QueryWrapper<OrderInfo>().select("distinct order_id").in("order_id", undetectedFakeOrderFragment.values().stream().map(FakeOrderInfo::getId).toList()));
                    result.forEach(orderInfoFromDatabase -> {
                        var fakeOrder = undetectedFakeOrderFragment.remove(orderInfoFromDatabase.getOrderId());
                        fakeOrder.setOrderPaymentTime(calendar.getTime());
                        detectedFakeOrderFragment.put(orderInfoFromDatabase.getOrderId(), fakeOrder);

                        var belongPurchased = monthFormat.format(fakeOrder.getOrderPaymentTime());
                        if (!detectedFakeOrderPurchasedDateMapFragment.containsKey(belongPurchased)) {
                            detectedFakeOrderPurchasedDateMapFragment.put(belongPurchased, new ArrayList<>());
                        }
                        detectedFakeOrderPurchasedDateMapFragment.get(belongPurchased).add(fakeOrder);
                    });
                    calendar.add(Calendar.DATE, 1);
                }

                System.out.println(undetectedFakeOrderFragment.size());
                System.out.println(detectedFakeOrderFragment.size());
                System.out.println(detectedFakeOrderPurchasedDateMapFragment.size());

                //按诉求日期插入全部匹配到的补单
                if (!detectedFakeOrderFragment.isEmpty()) {
                    CustomTableNameHandler.customTableName.set("z_fakeorders_requested_" + monthFormat.format(dayFormat.parse(key)));
                    System.out.println(fakeOrderDao.replaceBatchSomeColumn(detectedFakeOrderFragment.values().stream().toList()));
                    CustomTableNameHandler.customTableName.set("fakeorders");
                    System.out.println(fakeOrderDao.delete(new QueryWrapper<FakeOrderInfo>().in("id", detectedFakeOrderFragment.keySet())));
                }
                //按订单支付日期插入全部匹配到的补单
                for (var _entry : detectedFakeOrderPurchasedDateMapFragment.entrySet()) {
                    var belongPurchased = _entry.getKey();
                    var list = _entry.getValue();
                    CustomTableNameHandler.customTableName.set("z_fakeorders_purchased_" + belongPurchased);
                    System.out.println(fakeOrderDao.replaceBatchSomeColumn(list));
                }
                //插入未匹配到的补单
                if (!undetectedFakeOrderFragment.isEmpty()) {
                    CustomTableNameHandler.customTableName.set("fakeorders");
                    System.out.println(fakeOrderDao.replaceBatchSomeColumn(undetectedFakeOrderFragment.values().stream().toList()));
                }

            }
        }
    }


    public void _personalFakeOrderFileProcess(Sheet sheet, FileState state) {
        state.setCode(1); //processing
        state.setState("解析中（个人补单退款）");
        UploadWebSocket.sendStateToAll(state, true);

        var realRowNum = state.getRealRowNum();

        var personalFakeOrderPurchased = new HashMap<String, ArrayList<PersonalFakeOrderInfo>>();

        Row row;
        for (var i = 1; i <= realRowNum; i++) {
            row = sheet.getRow(i);

            var orderId = Long.parseLong(row.getCell(0).getStringCellValue());
            var orderPaymentTime = row.getCell(1).getDateCellValue();

            var personalFakeOrderInfo = new PersonalFakeOrderInfo()
                    .setId(orderId)
                    .setOrderPaymentTime(orderPaymentTime);


            var belongPurchased = monthFormat.format(orderPaymentTime);

            if (!personalFakeOrderPurchased.containsKey(belongPurchased)) {
                personalFakeOrderPurchased.put(belongPurchased, new ArrayList<>());
            }
            personalFakeOrderPurchased.get(belongPurchased).add(personalFakeOrderInfo);
        }


        try {
            _personalFakeOrderIntoDatabase(personalFakeOrderPurchased);
        } catch (Exception e) {
            e.printStackTrace();
            state.setCode(-1);
            state.setState("个人补单退款 保存时发生了一些问题");
            return;
        }


        state.setCode(2); //成功
        state.setState(String.format("个人补单退款 完事了"));
    }


    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public void _personalFakeOrderIntoDatabase(HashMap<String, ArrayList<PersonalFakeOrderInfo>> personalFakeOrderPurchased) {
        _personalFakeOrderIntoDatabaseImpl(personalFakeOrderPurchased);
    }

    public void _personalFakeOrderIntoDatabaseImpl(HashMap<String, ArrayList<PersonalFakeOrderInfo>> personalFakeOrderPurchased) {
        for (var entry : personalFakeOrderPurchased.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            var size = value.size();
            for (int i = 0; i < size; i += 10000) {
                var personalFakeOrderRequestedListFragment = value.subList(i, Math.min(i + 10000, size));
                var undetectedPersonalFakeOrderFragment = personalFakeOrderRequestedListFragment.stream().collect(Collectors.toMap(PersonalFakeOrderInfo::getId, order -> order));
                var detectedPersonalFakeOrderFragment = new HashMap<Long, PersonalFakeOrderInfo>();
                var detectedFakeOrderPurchasedDateMapFragment = new HashMap<String, ArrayList<PersonalFakeOrderInfo>>();

                log.info("开始匹配");

                log.info("剩余未检测：" + undetectedPersonalFakeOrderFragment.size());
                if (undetectedPersonalFakeOrderFragment.isEmpty()) break;

                CustomTableNameHandler.customTableName.set("z_refundorders_purchased_" + key);
                var result = refundOrderDao.selectList(new QueryWrapper<RefundOrderInfo>().select("distinct order_id, refund_end_time, order_payment_time").isNotNull("refund_end_time").in("order_id", undetectedPersonalFakeOrderFragment.values().stream().map(PersonalFakeOrderInfo::getId).toList()));

                log.info(String.valueOf(result.size()));
                //log.info(undetectedPersonalFakeOrderFragment.toString());
                //log.info(result.toString());
                result.forEach(refundOrderInfoFromDatabase -> {
                    undetectedPersonalFakeOrderFragment.remove(refundOrderInfoFromDatabase.getOrderId());
                    var fakeOrder = new PersonalFakeOrderInfo().setId(refundOrderInfoFromDatabase.getOrderId()).setOrderPaymentTime(refundOrderInfoFromDatabase.getOrderPaymentTime()).setRefundEndTime(refundOrderInfoFromDatabase.getRefundEndTime());

                    detectedPersonalFakeOrderFragment.put(refundOrderInfoFromDatabase.getOrderId(), fakeOrder);

                    var belongFinished = monthFormat.format(refundOrderInfoFromDatabase.getRefundEndTime());
                    if (!detectedFakeOrderPurchasedDateMapFragment.containsKey(belongFinished)) {
                        detectedFakeOrderPurchasedDateMapFragment.put(belongFinished, new ArrayList<>());
                    }
                    detectedFakeOrderPurchasedDateMapFragment.get(belongFinished).add(fakeOrder);
                });
                System.out.println("--------------------");
                System.out.println(undetectedPersonalFakeOrderFragment.size());
                System.out.println(detectedPersonalFakeOrderFragment.size());
                System.out.println(detectedFakeOrderPurchasedDateMapFragment.size());
                //log.info(undetectedPersonalFakeOrderFragment.toString());
                //log.info(detectedPersonalFakeOrderFragment.toString());

                //按支付日期插入全部匹配到的个人补单退款
                if (!detectedPersonalFakeOrderFragment.isEmpty()) {
                    CustomTableNameHandler.customTableName.set("z_fakeorders_personal_purchased_" + key);
                    System.out.println(personalFakeOrderDao.replaceBatchSomeColumn(detectedPersonalFakeOrderFragment.values().stream().toList()));
                    CustomTableNameHandler.customTableName.set("fakeorders_personal");
                    System.out.println(personalFakeOrderDao.delete(new QueryWrapper<PersonalFakeOrderInfo>().in("id", detectedPersonalFakeOrderFragment.keySet())));
                }
                //按退款完结日期插入全部匹配到的补单
                System.out.println("---");
                for (var _entry : detectedFakeOrderPurchasedDateMapFragment.entrySet()) {
                    var belongFinished = _entry.getKey();
                    var list = _entry.getValue();

                    System.out.println(list.size());
                    CustomTableNameHandler.customTableName.set("z_fakeorders_personal_finished_" + belongFinished);
                    System.out.println(personalFakeOrderDao.replaceBatchSomeColumn(list));
                }
                //插入未匹配到的个人补单退款
                if (!undetectedPersonalFakeOrderFragment.isEmpty()) {
                    CustomTableNameHandler.customTableName.set("fakeorders_personal");
                    System.out.println(personalFakeOrderDao.replaceBatchSomeColumn(undetectedPersonalFakeOrderFragment.values().stream().toList()));
                }

            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public JSONObject getMismatchFakeOrders(Order.GetPageDTO dto) {
        var page = new Page<FakeOrderInfo>(dto.getPage(), dto.getItemsPerPage());
        CustomTableNameHandler.customTableName.set("fakeorders");
        fakeOrderDao.selectPage(page, null);
        return new JSONObject().fluentPut("fakeorders", page.getRecords()).fluentPut("total", page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public JSONObject getMismatchPersonalFakeOrders(Order.GetPageDTO dto) {
        var page = new Page<PersonalFakeOrderInfo>(dto.getPage(), dto.getItemsPerPage());
        CustomTableNameHandler.customTableName.set("fakeorders_personal");
        personalFakeOrderDao.selectPage(page, null);
        return new JSONObject().fluentPut("fakeorders", page.getRecords()).fluentPut("total", page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public JSONObject getMismatchRefundOrders(Order.GetPageDTO dto) {
        var page = new Page<RefundOrderInfo>(dto.getPage(), dto.getItemsPerPage());
        CustomTableNameHandler.customTableName.set("refundorders");
        refundOrderDao.selectPage(page, null);
        return new JSONObject().fluentPut("refundorders", page.getRecords()).fluentPut("total", page.getTotal());
    }


    //内部无事务
    public void _tryMatchMisMatchAllPersonalFakeOrders() {
        CustomTableNameHandler.customTableName.set("fakeorders_personal");
        var allPersonalFakeOrders = personalFakeOrderDao.selectList(null);
        var personalFakeOrderPurchased = new HashMap<String, ArrayList<PersonalFakeOrderInfo>>();
        allPersonalFakeOrders.forEach(personalFakeOrderInfo -> {
            var belongPurchased = monthFormat.format(personalFakeOrderInfo.getOrderPaymentTime());
            if (!personalFakeOrderPurchased.containsKey(belongPurchased)) {
                personalFakeOrderPurchased.put(belongPurchased, new ArrayList<>());
            }
            personalFakeOrderPurchased.get(belongPurchased).add(personalFakeOrderInfo);
        });
        _personalFakeOrderIntoDatabaseImpl(personalFakeOrderPurchased);
    }


    //无需事务
    public void _tryMatchMisMatchAllProducts() {
        var mismatchProducts = mismatchProductDao.selectList(new QueryWrapper<MismatchProductInfo>().select("id"));
        var products = productDao.selectList(new QueryWrapper<ProductInfo>().select("id"));
        var matchedProductIds = new ArrayList<Long>();

        var mismatchProductsIds = mismatchProducts.stream().map(MismatchProductInfo::getId).collect(Collectors.toSet());
        var productIds = products.stream().map(ProductInfo::getId).collect(Collectors.toSet());


        mismatchProductsIds.forEach(id -> {
            if (productIds.contains(id)) {
                matchedProductIds.add(id);
            }
        });

        if (!matchedProductIds.isEmpty()) {
            mismatchProductDao.delete(new QueryWrapper<MismatchProductInfo>().in("id", matchedProductIds));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int deleteFakeOrderByIds(String ids) {
        var _ids = ids.split(",");
        CustomTableNameHandler.customTableName.set("fakeorders");
        return fakeOrderDao.delete(new QueryWrapper<FakeOrderInfo>().in("id", List.of(_ids)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int deletePersonalFakeOrderByIds(String ids) {
        var _ids = ids.split(",");
        CustomTableNameHandler.customTableName.set("fakeorders_personal");
        return personalFakeOrderDao.delete(new QueryWrapper<PersonalFakeOrderInfo>().in("id", List.of(_ids)));
    }


}

