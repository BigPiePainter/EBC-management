package com.pofa.ebcadmin.userLogin.service.impl;

import com.pofa.ebcadmin.product.dao.SkuDao;
import com.pofa.ebcadmin.product.entity.SkuInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class test{

    @Autowired
    public SkuDao skuDao;

    @Autowired
    public SkuInfo skuInfo;

    private static File excel = new File("C:\\Users\\ThinkPad\\Desktop\\test.xlsx");
    private static Workbook workbook;
    private static Sheet sheet;

    public static void main(String[] args) {

        try (InputStream fileInputStream = new FileInputStream(excel)) {
            workbook = WorkbookFactory.create(fileInputStream);
            sheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Excel 文件打开失败");
            System.exit(0);
        }

        log.info("打开成功");

        for (int j = 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);

            boolean flag = false;
            for (int i = 0; i < 6; i++) {
                Cell cell = row.getCell(i);
                if (cell == null) continue;

                switch (cell.getCellType().toString()) {
                    case "NUMERIC":
                        cell.getNumericCellValue();
                        break;
                    case "STRING":
                        if (!cell.getStringCellValue().equals("")) {
                            flag = true;
                            continue;
                        }
                        break;
                    default:
                        log.info("没见过的CellType");
                        log.info("数据类型:" + cell.getCellType());
                }

            }
            if (!flag) break;
            log.info(String.valueOf(row.getRowNum()));

           /* if (row.getCell(4).getCellType() == CellType.NUMERIC) {
                Date st = row.getCell(4).getDateCellValue();
                log.info("日期数据类型:" + row.getCell(4).getCellType());
                log.info("日期:" + st);
            } else {
                log.info("日期数据类型:" + row.getCell(4).getCellType());
                log.info("日期错误");
            }*/

            /*skuDao.insert(skuInfo
                    .setSku_id(row.getCell(1).getStringCellValue())
                    .setSku_name(row.getCell(2).getStringCellValue())
                    .setCost(Double.valueOf(row.getCell(4).getStringCellValue()))
                    .setPrice(Double.valueOf(row.getCell(3).getStringCellValue()))
                    .setEnd_time(Double.valueOf(row.getCell(6).getStringCellValue()))
                    .setStart_time(Double.valueOf(row.getCell(5).getStringCellValue()))
                    .setProduct_id(row.getCell(0).getStringCellValue()));
            */
            log.info("SKU ID：" + row.getCell(1).getStringCellValue());
            log.info("SKU名称：" + row.getCell(2).getStringCellValue());
            log.info("单个成本：" + row.getCell(4).getNumericCellValue());
            log.info("售卖价：" + row.getCell(3).getNumericCellValue());
            log.info("价格截止时间：" + row.getCell(6).getDateCellValue());
            log.info("价格开始时间：" + row.getCell(5).getDateCellValue());
            log.info("商品ID：" + row.getCell(0).getStringCellValue());
        }

            /*for (Cell cell : row){

                try{
                    log.info(cell.getStringCellValue());
                } catch (Exception ignore){

                }

            }*/
    }


public List<SkuInfo> skuReturn(String product_id){
        return null;
    }

    public int skuDelete(String sku_id){
        return 1;
    }

    public int skuEdit(String product_id,
                       String sku_id,
                       String sku_name,
                       Double price,
                       Double cost,
                       Double start_time,
                       Double end_time) {
        return 1;
    }

    public int skuAdd(String product_id,
                      String sku_id,
                      String sku_name,
                      Double price,
                      Double cost,
                      Double start_time,
                      Double end_time) {
        return 1;
    }

}
