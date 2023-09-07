/**
 * Copyright 2013-2015 JEECG (jeecgos@163.com)
 *   
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.com.tzy.springbootstarterautopoi.style;

import cn.afterturn.easypoi.excel.export.styler.AbstractExcelExportStyler;
import cn.afterturn.easypoi.excel.export.styler.IExcelExportStyler;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

/**
 * 带有边框的Excel样式
 * 
 * @author JEECG
 * @date 2015年1月9日 下午5:55:29
 */
public class ExcelExportStylerBorderImpl extends AbstractExcelExportStyler implements IExcelExportStyler {

	public ExcelExportStylerBorderImpl(Workbook workbook) {
		super.createStyles(workbook);
	}

	@Override
	public CellStyle getHeaderStyle(short color) {
		CellStyle titleStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 12);
		titleStyle.setFont(font);
		titleStyle.setBorderLeft(BorderStyle.THIN); // 左边框
		titleStyle.setBorderRight(BorderStyle.THIN); // 右边框
		titleStyle.setBorderBottom(BorderStyle.THIN);
		titleStyle.setBorderTop(BorderStyle.THIN);
		titleStyle.setAlignment(HorizontalAlignment.CENTER);
		titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		return titleStyle;
	}

	@Override
	public CellStyle stringNoneStyle(Workbook workbook, boolean isWarp) {
		CellStyle style = workbook.createCellStyle();
		style.setBorderLeft(BorderStyle.THIN); // 左边框
		style.setBorderRight(BorderStyle.THIN); // 右边框
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setDataFormat(STRING_FORMAT);
		if (isWarp) {
			style.setWrapText(true);
		}
		return style;
	}

	@Override
	public CellStyle getTitleStyle(short color) {
		CellStyle titleStyle = workbook.createCellStyle();
		titleStyle.setBorderLeft(BorderStyle.THIN); // 左边框
		titleStyle.setBorderRight(BorderStyle.THIN); // 右边框
		titleStyle.setBorderBottom(BorderStyle.THIN);
		titleStyle.setBorderTop(BorderStyle.THIN);
		titleStyle.setAlignment(HorizontalAlignment.CENTER);
		titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		titleStyle.setWrapText(true);
		return titleStyle;
	}

	@Override
	public CellStyle stringSeptailStyle(Workbook workbook, boolean isWarp) {
		return isWarp ? stringNoneWrapStyle : stringNoneStyle;
	}

	public static void setBorderStyle(int border, CellRangeAddress region, Sheet sheet){
		RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);  //下边框
		RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);     //左边框
		RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);    //右边框
		RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);      //上边框
	}
}
