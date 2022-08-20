package com.pofa.ebcadmin.order.service.impl;

import com.pofa.ebcadmin.order.dao.FakeOrderDao;
import com.pofa.ebcadmin.order.dao.OrderDao;
import com.pofa.ebcadmin.order.entity.FakeOrderInfo;
import com.pofa.ebcadmin.order.entity.OrderInfo;
import com.pofa.ebcadmin.order.orderUtils.FileState;
import com.pofa.ebcadmin.order.orderUtils.FileStateManager;
import com.pofa.ebcadmin.order.orderUtils.OrderFileUtils;
import com.pofa.ebcadmin.order.service.OrderService;
import com.pofa.ebcadmin.product.entity.SkuInfo;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import static com.pofa.ebcadmin.order.orderUtils.OrderFileUtils.isBlankCell;
import static com.pofa.ebcadmin.order.orderUtils.OrderFileUtils.isValidFileType;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    public OrderDao orderDao;

    @Autowired
    public OrderInfo orderInfo;

    @Autowired
    public FakeOrderDao fakeOrderDao;

    @Autowired
    public FakeOrderInfo fakeOrderInfo;


    public String[] orderHeader = {"子订单编号", "主订单编号", "买家会员名", "买家会员ID", "支付单号", "买家应付货款", "买家应付邮费", "总金额", "买家实际支付金额", "子单实际支付金额", "订单状态", "收件人姓名", "收货地址", "联系手机", "订单创建时间", "订单付款时间", "宝贝标题", "宝贝数量", "物流单号", "物流公司", "店铺ID", "店铺名称", "供应商ID", "供应商名称", "仓发类型", "退款金额", "颜色/尺码", "商家编码", "商品编码"};
    public String[] fakeOrderHeader = {"序号", "会员号", "订单编号", "价格", "价格合计", "佣金", "佣金合计", "诉求日期", "佣金", "品数", "本单佣金", "团队"};


    @Override
    public void fileProcess(MultipartFile file) {
        var state = new FileState().setFileName(file.getOriginalFilename()).setSize(file.getSize()).setState("排队中");
        FileStateManager.newFile(state.getFileName(), state);

        //fileState会保留10分钟
        new Thread(() -> {
            try {
                Thread.sleep(1000 * 60 * 10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                FileStateManager.removeFile(state.getFileName());
            }
        }).start();

        try (InputStream inputStream = file.getInputStream()) {
            _fileProcess(state, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Excel 文件打开失败");
        }

    }


    public synchronized void _fileProcess(FileState state, InputStream inputStream) throws IOException {
        System.out.println(state.getFileName() + " 开始处理");

        state.setCode(1); //processing
        state.setState("文件打开中");

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            System.out.println("打开成功");
            var sheet = workbook.getSheetAt(0);

            //识别上传的订单类型
            //订单信息
            if (isValidFileType(sheet, orderHeader)) {
                _orderFileProcess(sheet, state);
                return;
            }
            //刷单信息
            if (isValidFileType(sheet, fakeOrderHeader)) {
                _fakeOrderFileProcess(sheet, state);
                return;
            }

            state.setCode(-1); //error
            state.setState("没有匹配到任何文件类型");
        } catch (IOException e) {
            e.printStackTrace();
            state.setCode(-1); //error
            state.setState("Excel 文件打开失败");
            System.out.println("Excel 文件打开失败");
        }
        //System.out.println(state.getFileName() + " 处理完毕");
    }


    public void _orderFileProcess(Sheet sheet, FileState state) {
        state.setCode(1); //processing
        state.setState("解析中（补单）");

        state.setCode(2); //成功
        state.setState("成功");
    }

    public void _fakeOrderFileProcess(Sheet sheet, FileState state) {
        state.setCode(1); //processing
        state.setState("解析中（补单）");

        var today = Calendar.getInstance();
        var earliestDay = Calendar.getInstance();
        earliestDay.set(2022, Calendar.FEBRUARY, 1);

        var totalRow = sheet.getLastRowNum();

        //判断表格信息合法性
        Row row;
        Cell cellA, cellB, cellC, cellD, cellE;
        CellType typeA, typeB, typeC, typeD, typeE;
        boolean isABlank, isBBlank, isCBlank, isDBlank, isEBlank;

        var touchBottom = 0;
        var wrong = false;
        for (var i = 1; i < totalRow; i++) {
            row = sheet.getRow(i);

//            System.out.println(i);

            cellA = row.getCell(2);
            cellB = row.getCell(7);
            cellC = row.getCell(9);
            cellD = row.getCell(10);
            cellE = row.getCell(11);

            isABlank = isBlankCell(cellA);
            isBBlank = isBlankCell(cellB);
            isCBlank = isBlankCell(cellC);
            isDBlank = isBlankCell(cellD);
            isEBlank = isBlankCell(cellE);

            if (touchBottom > 0) {

                if (!isABlank) {
                    System.out.println("有问题");
                    wrong = true;
                    state.setState((i + 1) + "C 不应该有东西才对");
                    break;
                }
                if (!isBBlank) {
                    System.out.println("有问题");
                    wrong = true;
                    state.setState((i + 1) + "H 不应该有东西才对");
                    break;
                }
                if (!isCBlank) {
                    System.out.println("有问题");
                    wrong = true;
                    state.setState((i + 1) + "J 不应该有东西才对");
                    break;
                }
                if (!isDBlank) {
                    System.out.println("有问题");
                    wrong = true;
                    state.setState((i + 1) + "K 不应该有东西才对");
                    break;
                }
                if (!isEBlank) {
                    System.out.println("有问题");
                    wrong = true;
                    state.setState((i + 1) + "L 不应该有东西才对");
                    break;
                }


            } else {


                if (isABlank && isBBlank && isCBlank && isDBlank && isEBlank) {
                    System.out.println("TouchButtom");
                    touchBottom = i;
                    continue;
                }


                if (isABlank || isBBlank || isCBlank || isDBlank || isEBlank) {
                    wrong = true;
                    state.setState("第" + (i + 1) + "行有数据丢失");
                    break;
                }


                typeA = cellA.getCellType();
                typeB = cellB.getCellType();
                typeC = cellC.getCellType();
                typeD = cellD.getCellType();
                typeE = cellE.getCellType();

//                System.out.println(typeA);
//                System.out.println(typeB);
//                System.out.println(typeC);
//                System.out.println(typeD);
//                System.out.println(typeE);

                if (typeA != CellType.STRING || typeB != CellType.NUMERIC || typeC != CellType.FORMULA || typeD != CellType.FORMULA || typeE != CellType.STRING) {
                    System.out.println("有问题");
                    wrong = true;
                    state.setState("第" + (i + 1) + "行某些数据的格式好像有点问题");
                    break;
                } else {
                    if (cellA.getStringCellValue().length() != 19) {
                        wrong = true;
                        state.setState("第" + (i + 1) + "行的订单ID长度不对吧！");
                        break;
                    }
                    if (today.getTime().before(cellB.getDateCellValue())) {
                        wrong = true;
                        state.setState("第" + (i + 1) + "行的诉求日期是未来？");
                        break;
                    }
                    if (earliestDay.getTime().after(cellB.getDateCellValue())) {
                        wrong = true;
                        state.setState("第" + (i + 1) + "行的诉求日期有点早");
                        break;
                    }
                }
            }


        }


        System.out.println("校验完毕");
        System.out.println(wrong);
        System.out.println(touchBottom);

        if (wrong) {
            state.setCode(-1);
            return;
        }

        state.setState("插入数据库");


        //插入数据库
        var list = new ArrayList<FakeOrderInfo>();
        var count = 0;
        for (var i = 1; i < touchBottom; i++) {
            row = sheet.getRow(i);

            cellA = row.getCell(2);
            cellB = row.getCell(7);
            cellC = row.getCell(9);
            cellD = row.getCell(10);
            cellE = row.getCell(11);

//            System.out.println(cellA.getStringCellValue());
//            System.out.println(cellB.getDateCellValue());
//            System.out.println(cellC.getNumericCellValue());
//            System.out.println(cellD.getNumericCellValue());
//            System.out.println(cellE.getStringCellValue());


            list.add(new FakeOrderInfo()
                    .setOrderId(Long.parseLong(cellA.getStringCellValue()))
                    .setRequestTime(cellB.getDateCellValue())
                    .setProductCount(Double.valueOf(cellC.getNumericCellValue()).longValue())
                    .setBrokerage(BigDecimal.valueOf(cellD.getNumericCellValue()))
                    .setTeam(cellE.getStringCellValue())
            );
            if ((i + 1) % 3000 == 0) {
                count += fakeOrderDao.replaceBatchSomeColumn(list);
                list.clear();
            }
        }

        if (!list.isEmpty()) {
            count += fakeOrderDao.replaceBatchSomeColumn(list);
        }
        System.out.println(count);

        //orderDao.replaceBatchSomeColumn()

        var newData = 2 * touchBottom - count - 2;
        var replaceData = touchBottom - newData - 1;

        state.setCode(2); //成功
        state.setState(String.format("补单解析成功 %s 条（覆盖%s，新增%s）", touchBottom - 1, replaceData, newData));
    }
}

