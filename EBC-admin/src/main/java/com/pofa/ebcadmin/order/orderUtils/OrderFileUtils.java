package com.pofa.ebcadmin.order.orderUtils;

import com.pofa.ebcadmin.globalSocket.utils.GlobalWebSocket;
import com.pofa.ebcadmin.utils.webSocket.WebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

@Slf4j
public class OrderFileUtils {
    //这些方法的参数设计的很随意

    public static boolean isValidFileType(Sheet sheet, String[] titles) {
        var topRow = sheet.getRow(0);
        if (null == topRow) return false;
        var col = 0;
        for (var title : titles) {
            if (null == topRow.getCell(col) || topRow.getCell(col).getCellType() != CellType.STRING) {
                return false;
            }
            if (!topRow.getCell(col++).getStringCellValue().equals(title)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBlankCell(Cell cell) {
        if (null == cell) return true;
        return switch (cell.getCellType()) {
            case BLANK -> true;
            case STRING -> cell.getStringCellValue().isBlank();
            default -> false;
        };
    }

    public static boolean isBlankRow(Row row, int limit) {
        if (null == row) return true;
        for (int i = 0; i <= limit; i++) {
            if (!isBlankCell(row.getCell(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean orderPreProcess(Sheet sheet, FileState state) {

        state.setState("订单 格式校验中");
        UploadWebSocket.sendStateToAll(state, true);

        var today = Calendar.getInstance();
        var earliestDay = Calendar.getInstance();
        earliestDay.set(2022, Calendar.FEBRUARY, 1);

        var lastRowNum = sheet.getLastRowNum();
        //判断表格信息合法性

        var notNullCellCols = new int[]{0, 1, 4, 5, 6, 7, 8, 9, 10, 14, 15, 16, 17, 20, 21, 22, 23, 24, 25, 26, 28};
        var notNullCellColsValidCellType = new CellType[]{CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING};

        var rightRowNum = 0;
        var wrongRowNum = 0;

        Row row;
        for (var rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
            state.setState(String.format("订单 格式校验中 正确: %s行 忽略: %s行", rightRowNum, wrongRowNum));
            UploadWebSocket.sendStateToAll(state);

            row = sheet.getRow(rowIndex);
            if (isBlankRow(row, 21)) {
                //log.info("BlankRow");
                wrongRowNum++;
                continue;
            }

            for (int i = 0; i < notNullCellCols.length; i++) {
                if (isBlankCell(row.getCell(notNullCellCols[i]))) {
                    if (notNullCellCols[i] == 25) { //退款金额 为空时，按0处理
                        row.getCell(notNullCellCols[i]).setCellValue("0.0");
                    } else if (notNullCellCols[i] == 4) { //支付单号 为空时，删除整行
                        sheet.removeRow(row);
                        //log.info("删除了一整行" + (rowIndex + 1));
                        wrongRowNum++;
                        break;
                    } else if (notNullCellCols[i] == 5) { //买家应付货款 为空时，删除整行
                        sheet.removeRow(row);
                        //log.info("删除了一整行" + (rowIndex + 1));
                        wrongRowNum++;
                        break;
                    } else {
                        state.setCode(-1);
                        state.setState("第" + (rowIndex + 1) + "行" + (notNullCellCols[i] + 1) + "列有数据丢失");
                        return false;
                    }
                }
                if (row.getCell(notNullCellCols[i]).getCellType() != notNullCellColsValidCellType[i]) {
                    log.info(String.valueOf(notNullCellCols[i]));
                    log.info(String.valueOf(row.getCell(notNullCellCols[i]).getCellType()));
                    log.info(String.valueOf(notNullCellColsValidCellType[i]));
                    state.setCode(-1);
                    state.setState("第" + (rowIndex + 1) + "行有数据格式错误");
                    return false;
                }
            }

            rightRowNum++;
        }


        //检查重复
        var realRowNum = 0;
        var set = new HashSet<String>();
        for (var rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
            row = sheet.getRow(rowIndex);
            if (isBlankRow(row, 21)) {
                continue;
            }

            set.add(row.getCell(0).getStringCellValue());
            realRowNum++;
        }

        log.info(String.valueOf(realRowNum));

        log.info("去重检查完毕");
        if (set.size() != realRowNum) {
            state.setCode(-1);
            state.setState("订单 有子订单id重复的数据, 准确来说重复了 " + (realRowNum - set.size()) + " 个");
            return false;
        }

        state.setRightRowNum(rightRowNum);
        state.setWrongRowNum(wrongRowNum);
        state.setRealRowNum(realRowNum);


        return true;

    }

    //检查所有格式的合法性并计算真实数据行数
    public static boolean refundOrderPreProcess(Sheet sheet, FileState state) {
        state.setState("退单 格式校验中");

        var today = Calendar.getInstance();
        var earliestDay = Calendar.getInstance();
        earliestDay.set(2022, Calendar.FEBRUARY, 1);

        var lastRowNum = sheet.getLastRowNum();
        //判断表格信息合法性
        Row row;

        var notNullCellCols = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 13, 18};
        var notNullCellColsValidCellType = new CellType[]{CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.NUMERIC, CellType.NUMERIC, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING, CellType.STRING};

        var touchBottom = false;
        var realRowNum = 0;
        for (var rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
            row = sheet.getRow(rowIndex);
            if (touchBottom) {
                for (int j = 0; j <= 21; j++) {
                    if (!isBlankCell(row.getCell(j))) {
                        state.setCode(-1);
                        state.setState((rowIndex + 1) + "行 不应该有东西才对");
                        return false;
                    }
                }
            } else {
                if (isBlankRow(row, 21)) {
                    log.info("TouchButtom");
                    touchBottom = true;
                    continue;
                }
                realRowNum = rowIndex;

                for (int i = 0; i < notNullCellCols.length; i++) {
                    if (isBlankCell(row.getCell(notNullCellCols[i]))) {
                        state.setCode(-1);
                        state.setState("第" + (rowIndex + 1) + "行" + (notNullCellCols[i] + 1) + "列有数据丢失");
                        return false;
                    }
                    if (row.getCell(notNullCellCols[i]).getCellType() != notNullCellColsValidCellType[i]) {
                        log.info(String.valueOf(notNullCellCols[i]));
                        log.info(String.valueOf(row.getCell(notNullCellCols[i]).getCellType()));
                        log.info(String.valueOf(notNullCellColsValidCellType[i]));
                        state.setCode(-1);
                        state.setState("第" + (rowIndex + 1) + "行有数据格式错误");
                        return false;
                    }
                }


            }


        }

        //检查重复
        log.info(String.valueOf(realRowNum));
        var set = new HashSet<String>();
        for (var i = 1; i <= realRowNum; i++) {
            set.add(sheet.getRow(i).getCell(1).getStringCellValue());
        }
        log.info("去重检查完毕");
        if (set.size() != realRowNum) {
            state.setCode(-1);
            state.setState("退单 有退单编号重复的数据, 准确来说重复了 " + (realRowNum - set.size()) + " 个");
            return false;
        }

        state.setRealRowNum(realRowNum);
        return true;

    }


    //检查所有格式的合法性并计算真实数据行数
    public static boolean fakeOrderPreProcess(Sheet sheet, FileState state) {
        state.setState("补单 格式校验中");
        state.setCode(-1);

        var today = Calendar.getInstance();
        var earliestDay = Calendar.getInstance();
        earliestDay.set(2022, Calendar.FEBRUARY, 1);

        var totalRow = sheet.getLastRowNum();
        //判断表格信息合法性
        Row row;
        Cell cellA, cellB, cellC, cellD, cellE;
        CellType typeA, typeB, typeC, typeD, typeE;
        boolean isABlank, isBBlank, isCBlank, isDBlank, isEBlank;

        var touchBottom = false;
        var realRowNum = 0;
        for (var i = 1; i < totalRow; i++) {
            row = sheet.getRow(i);
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
            if (touchBottom) {
                if (!isABlank || !isBBlank || !isCBlank || !isDBlank || !isEBlank) {
                    state.setState((i + 1) + "行 不应该有东西才对");
                    return false;
                }
            } else {
                if (isABlank && isBBlank && isCBlank && isDBlank && isEBlank) {
                    log.info("TouchButtom");
                    touchBottom = true;
                    continue;
                }
                realRowNum = i;
                if (isABlank || isBBlank || isCBlank || isDBlank || isEBlank) {
                    state.setState("第" + (i + 1) + "行有数据丢失");
                    return false;
                }
                typeA = cellA.getCellType();
                typeB = cellB.getCellType();
                typeC = cellC.getCellType();
                typeD = cellD.getCellType();
                typeE = cellE.getCellType();
                if (typeA != CellType.STRING || typeB != CellType.NUMERIC || (typeC != CellType.FORMULA && typeC != CellType.NUMERIC) || (typeD != CellType.FORMULA && typeD != CellType.NUMERIC) || typeE != CellType.STRING) {
                    log.info("有问题");
                    state.setState("第" + (i + 1) + "行某些数据的格式好像有点问题");
                    return false;
                } else {
                    if (cellA.getStringCellValue().length() != 19) {
                        state.setState("第" + (i + 1) + "行的订单ID长度不对吧！");
                        return false;
                    }
                    if (today.getTime().before(cellB.getDateCellValue())) {
                        state.setState("第" + (i + 1) + "行的诉求日期是未来？");
                        return false;
                    }
                    if (earliestDay.getTime().after(cellB.getDateCellValue())) {
                        state.setState("第" + (i + 1) + "行的诉求日期有点早");
                        return false;
                    }
                }
            }


        }


        //检查重复
        log.info(String.valueOf(realRowNum));
        var set = new HashSet<String>();
        for (var i = 1; i <= realRowNum; i++) {
            set.add(sheet.getRow(i).getCell(2).getStringCellValue());
        }
        log.info("去重检查完毕");
        if (set.size() != realRowNum) {
            state.setCode(-1);
            state.setState("补单 有订单编号重复的数据, 准确来说重复了 " + (realRowNum - set.size()) + " 个");
            return false;
        }

        state.setRealRowNum(realRowNum);
        return true;
    }


    //检查所有格式的合法性并计算真实数据行数
    public static boolean personalFakeOrderPreProcess(Sheet sheet, FileState state) {
        state.setState("个人补单退款 格式校验中");
        state.setCode(-1);
        UploadWebSocket.sendStateToAll(state, true);

        var today = Calendar.getInstance();
        var earliestDay = Calendar.getInstance();
        earliestDay.set(2022, Calendar.FEBRUARY, 1);

        var totalRow = sheet.getLastRowNum();
        //判断表格信息合法性
        Row row;
        Cell cellA, cellB;
        CellType typeA, typeB;
        boolean isABlank, isBBlank;

        var touchBottom = false;
        var realRowNum = 0;

        var dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        for (var i = 1; i < totalRow; i++) {
            row = sheet.getRow(i);
            cellA = row.getCell(0);
            cellB = row.getCell(1);
            isABlank = isBlankCell(cellA);
            isBBlank = isBlankCell(cellB);
            if (touchBottom) {
                if (!isABlank || !isBBlank) {
                    state.setState((i + 1) + "行 不应该有东西才对");
                    return false;
                }
            } else {
                if (isABlank && isBBlank) {
                    log.info("TouchButtom");
                    touchBottom = true;
                    continue;
                }
                realRowNum = i;
                if (isABlank || isBBlank) {
                    state.setState("第" + (i + 1) + "行有数据丢失");
                    return false;
                }
                typeA = cellA.getCellType();
                typeB = cellB.getCellType();
                if (typeB == CellType.STRING){
                    var str = cellB.getStringCellValue();
                    try {
                        cellB.setCellValue(dateTimeFormat.parse(str));
                        typeB = cellB.getCellType();
                    } catch (ParseException e) {
                        state.setState("第" + (i + 1) + "行的日期格式好像有点问题");
                        return false;
                    }
                }
                if (typeA != CellType.STRING || typeB != CellType.NUMERIC) {
                    log.info("有问题");
                    log.info(String.valueOf(typeA));
                    log.info(String.valueOf(typeB));
                    state.setState("第" + (i + 1) + "行某些数据的格式好像有点问题");
                    return false;
                }
            }


        }


        //检查重复
        log.info(String.valueOf(realRowNum));
        var set = new HashSet<String>();
        for (var i = 1; i <= realRowNum; i++) {
            set.add(sheet.getRow(i).getCell(0).getStringCellValue());
        }
        log.info("去重检查完毕");
        if (set.size() != realRowNum) {
            state.setCode(-1);
            state.setState("补单 有订单号重复的数据, 准确来说重复了 " + (realRowNum - set.size()) + " 个");
            return false;
        }

        state.setRealRowNum(realRowNum);
        return true;
    }
}
