package com.pofa.ebcadmin.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pofa.ebcadmin.order.dao.OrderDao;
import com.pofa.ebcadmin.order.dto.Order;
import com.pofa.ebcadmin.order.entity.OrderInfo;
import com.pofa.ebcadmin.order.orderUtils.FileState;
import com.pofa.ebcadmin.order.orderUtils.FileStateManager;
import com.pofa.ebcadmin.order.service.OrderService;
import com.pofa.ebcadmin.utils.process.ProcessLock;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    public OrderDao orderDao;

    @Autowired
    public OrderInfo orderInfo;


    public String[] orderHeader = {"子订单编号", "主订单编号", "买家会员名", "买家会员ID", "支付单号", "买家应付货款", "买家应付邮费", "总金额", "买家实际支付金额", "子单实际支付金额", "订单状态", "收件人姓名", "收货地址", "联系手机", "订单创建时间", "订单付款时间", "宝贝标题", "宝贝数量", "物流单号", "物流公司", "店铺ID", "店铺名称", "供应商ID", "供应商名称", "仓发类型", "退款金额", "颜色/尺码", "商家编码", "商品编码"};

    public String[] fakeOrderHeader = {"序号", "会员号", "订单编号", "价格", "价格合计", "佣金", "佣金合计", "诉求日期", "佣金", "品数", "本单佣金", "团队"};


    @Override
    public void fileProcess(MultipartFile file) {
        var state = new FileState().setFileName(file.getOriginalFilename()).setSize(file.getSize()).setState("waiting");
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
            var topRow = sheet.getRow(0);

            if (null == topRow) {
                state.setCode(-1); //error
                state.setState("没有匹配到任何文件类型");
                return;
            }


            //识别上传的订单类型
            int col;
            boolean wrong;

            //订单信息
            col = 0;
            wrong = false;
            for (var header : orderHeader) {
                if (null == topRow.getCell(col) || topRow.getCell(col).getCellType() != CellType.STRING) {
                    wrong = true;
                    break;
                }
                if (!topRow.getCell(col++).getStringCellValue().equals(header)) {
                    wrong = true;
                    break;
                }
            }
            if (!wrong) {
                _orderFileProcess(state, sheet);
                return;
            }

            //刷单信息
            col = 0;
            wrong = false;
            for (var header : fakeOrderHeader) {
                if (null == topRow.getCell(col) || topRow.getCell(col).getCellType() != CellType.STRING) {
                    wrong = true;
                    break;
                }
                if (!topRow.getCell(col++).getStringCellValue().equals(header)) {
                    wrong = true;
                    break;
                }
            }
            if (!wrong) {
                _fakeOrderFileProcess(state, sheet);
                return;
            }

            state.setCode(-1); //error
            state.setState("没有匹配到任何文件类型");
        } catch (IOException e) {
            e.printStackTrace();
            state.setCode(-1); //error
            state.setState("Excel 文件打开失败");
            System.out.println("Excel 文件打开失败");
            return;
        }
        //System.out.println(state.getFileName() + " 处理完毕");
    }


    public void _orderFileProcess(FileState state, Sheet sheet) {
        state.setCode(1); //processing
        state.setState("解析中（补单）");

        state.setCode(2); //成功
        state.setState("成功");
    }

    public void _fakeOrderFileProcess(FileState state, Sheet sheet) {
        state.setCode(1); //processing
        state.setState("解析中（补单）");

        var totalRow = sheet.getLastRowNum();

        //判断表格信息合法性
        Row row;
        Cell cellA, cellB, cellC, cellD, cellE;
        CellType typeA, typeB, typeC, typeD, typeE;

        var touchBottom = false;
        var wrong = false;
        for (var i = 0; i < totalRow; i++) {
            row = sheet.getRow(i);

            System.out.println(i);

            cellA = row.getCell(2);
            cellB = row.getCell(7);
            cellC = row.getCell(9);
            cellD = row.getCell(10);
            cellE = row.getCell(11);

            if (touchBottom) {
                if (null != cellA) {
                    typeA = cellA.getCellType();
                    if (typeA != CellType.BLANK) {
                        System.out.println("有问题");
                        wrong = true;
                        state.setState("第" + i + "行，出现意料之外的内容");
                        break;
                    }
                }
                if (null != cellB) {
                    typeB = cellB.getCellType();
                    if (typeB != CellType.BLANK) {
                        System.out.println("有问题");
                        wrong = true;
                        state.setState("第" + i + "行，出现意料之外的内容");
                        break;
                    }
                }
                if (null != cellC) {
                    typeC = cellC.getCellType();
                    if (typeC != CellType.BLANK) {
                        System.out.println("有问题");
                        wrong = true;
                        state.setState("第" + i + "行，出现意料之外的内容");
                        break;
                    }
                }
                if (null != cellD) {
                    typeD = cellD.getCellType();
                    if (typeD != CellType.BLANK) {
                        System.out.println("有问题");
                        wrong = true;
                        state.setState("第" + i + "行，出现意料之外的内容");
                        break;
                    }
                }
                if (null != cellE) {
                    typeE = cellE.getCellType();
                    if (typeE != CellType.BLANK) {
                        System.out.println("有问题");
                        wrong = true;
                        state.setState("第" + i + "行，出现意料之外的内容");
                        break;
                    }
                }


            } else {
                if (null == cellA || null == cellB || null == cellC || null == cellD || null == cellE) {
                    System.out.println("TouchButtom");
                    touchBottom = true;
                    continue;
                }

                typeA = cellA.getCellType();
                typeB = cellB.getCellType();
                typeC = cellC.getCellType();
                typeD = cellD.getCellType();
                typeE = cellE.getCellType();

                System.out.println(typeA);
                System.out.println(typeB);
                System.out.println(typeC);
                System.out.println(typeD);
                System.out.println(typeE);

                if (typeA == CellType.BLANK && typeB == CellType.BLANK && typeC == CellType.BLANK && typeD == CellType.BLANK && typeE == CellType.BLANK) {
                    System.out.println("TouchButtom");
                    touchBottom = true;
                    continue;
                }

                if (typeA == CellType.BLANK || typeB == CellType.BLANK || typeC == CellType.BLANK || typeD == CellType.BLANK || typeE == CellType.BLANK) {
                    System.out.println("有问题");
                    wrong = true;
                    state.setState("第" + i + "行，疑似出现数据丢失");
                    break;
                }
            }


        }


        System.out.println("校验完毕");
        System.out.println(wrong);

        if (wrong) {
            state.setCode(-1);
            return;
        }


        state.setCode(2); //成功
        state.setState("成功");
    }


}

