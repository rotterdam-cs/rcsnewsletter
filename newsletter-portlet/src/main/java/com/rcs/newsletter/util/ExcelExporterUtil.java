package com.rcs.newsletter.util;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public class ExcelExporterUtil {

    /**
     * Method that create the cell style for the excel Header.
     * Puts the title center aligned and the font on bold style
     * @param workbook
     * @return 
     */
    public static HSSFCellStyle createHeaderStyle(final HSSFWorkbook workbook) {        
        final HSSFCellStyle cellStyle = workbook.createCellStyle();
        
        HSSFFont font = workbook.createFont();        
        font.setColor(HSSFColor.BLACK.index);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);        
        cellStyle.setFont(font);
        
        return cellStyle;
    }
}
