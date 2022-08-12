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
            _fileProcess(file, state, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Excel 文件打开失败");
        }

    }


    @Async
    public synchronized void _fileProcess(MultipartFile file, FileState state, InputStream inputStream) {
        System.out.println(state.getFileName() + " 开始处理");
        state.setState("processing");

        Workbook workbook;
        Sheet sheet;

        try {
            workbook = WorkbookFactory.create(inputStream);
            sheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Excel 文件打开失败");
            return;
        }

        System.out.println("打开成功");
        var count = 0;

        var maxRow = sheet.getLastRowNum();

        for (int i = 0; i < maxRow; i++) {
            var row = sheet.getRow(i);
            count++;
        }
        System.out.println("共" + count + "条目");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(state.getFileName() + " 处理完毕");
        state.setState("done");
    }

}

