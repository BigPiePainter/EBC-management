package com.pofa.ebcadmin.order.orderUtils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;

public class OrderFileUtils {
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
}
