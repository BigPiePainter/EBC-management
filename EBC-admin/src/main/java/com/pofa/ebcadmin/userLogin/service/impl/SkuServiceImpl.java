package com.pofa.ebcadmin.userLogin.service.impl;

import com.pofa.ebcadmin.userLogin.dao.SkuDao;
import com.pofa.ebcadmin.userLogin.entity.SkuInfo;
import com.pofa.ebcadmin.userLogin.service.SkuService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {

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
            System.out.println("Excel 文件打开失败");
            System.exit(0);
        }

        System.out.println("打开成功");

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
                        System.out.println("没见过的CellType");
                        System.out.println("数据类型:" + cell.getCellType());
                }

            }
            if (!flag) break;
            System.out.println(row.getRowNum());

           /* if (row.getCell(4).getCellType() == CellType.NUMERIC) {
                Date st = row.getCell(4).getDateCellValue();
                System.out.println("日期数据类型:" + row.getCell(4).getCellType());
                System.out.println("日期:" + st);
            } else {
                System.out.println("日期数据类型:" + row.getCell(4).getCellType());
                System.out.println("日期错误");
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
            System.out.println("SKU ID：" + row.getCell(1).getStringCellValue());
            System.out.println("SKU名称：" + row.getCell(2).getStringCellValue());
            System.out.println("单个成本：" + row.getCell(4).getNumericCellValue());
            System.out.println("售卖价：" + row.getCell(3).getNumericCellValue());
            System.out.println("价格截止时间：" + row.getCell(6).getDateCellValue());
            System.out.println("价格开始时间：" + row.getCell(5).getDateCellValue());
            System.out.println("商品ID：" + row.getCell(0).getStringCellValue());
        }

            /*for (Cell cell : row){

                try{
                    System.out.println(cell.getStringCellValue());
                } catch (Exception ignore){

                }

            }*/
    }


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, timeout = 30 * 1000)
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
