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

        state.setState("processing");

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            System.out.println("打开成功");
            var sheet = workbook.getSheetAt(0);
            var topRow = sheet.getRow(0);


            //识别上传的订单类型
            int col;
            boolean wrong;

            //订单信息
            col = 0;
            wrong = false;
            for (var header : orderHeader) {
                System.out.println(header);
                System.out.println(topRow.getCell(col).getStringCellValue());
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
                if (!topRow.getCell(col++).getStringCellValue().equals(header)) {
                    wrong = true;
                    break;
                }
            }
            if (!wrong) {
                _fakeOrderFileProcess(state, sheet);
                return;
            }


            state.setState("没有匹配到任何文件类型");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Excel 文件打开失败");
            return;
        }
        //System.out.println(state.getFileName() + " 处理完毕");
    }


    public void _orderFileProcess(FileState state, Sheet sheet) {


    }

    public void _fakeOrderFileProcess(FileState state, Sheet sheet) {
        var totalRow = sheet.getLastRowNum();
        Row row;
        var wrong = false;
        for (int i = 0; i < totalRow; i++) {
            row = sheet.getRow(i);
            //判断表格信息合法性

            //判断是不是空Row


            if (row.getCell(2).getCellType() != CellType.STRING){


            }



        }

    }


}

