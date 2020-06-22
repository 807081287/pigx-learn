/**
 * Copyright (C), 2019-2020, 成都房联云码科技有限公司
 * FileName: PoiUtils
 * Author:   Arron-wql
 * Date:     2020/6/17 10:58
 * Description: POI工具类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.pig4cloud.pigx.common.core.util;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * POI工具类
 *
 * @author qinglong.wu@funi365.com
 * @create 2020/6/17
 * @Version 1.0.0
 */
public class ExcelReaderUtils{

		/**
		 * Excel读取 操作
		 */
		public static List<List<String>> readExcel(InputStream is) throws IOException {
			Workbook wb = null;
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				wb = WorkbookFactory.create(is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			/** 得到第一个sheet */
			Sheet sheet = wb.getSheetAt(0);
			/** 得到Excel的行数 */
			int totalRows = sheet.getPhysicalNumberOfRows();

			/** 得到Excel的列数 */
			int totalCells = 0;
			if (totalRows >= 1 && sheet.getRow(0) != null) {
				totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
			}

			List<List<String>> dataLst = new ArrayList<List<String>>();
			/** 循环Excel的行 */
			for (int r = 0; r < totalRows; r++) {
				Row row = sheet.getRow(r);
				if (row == null) continue;
				if (isRowEmpty(row)) continue; //过滤空行
				List<String> rowLst = new ArrayList<String>();
				/** 循环Excel的列 */
				for (int c = 0; c < totalCells; c++) {
					Cell cell = row.getCell(c);
					String cellValue = "";
					if (null != cell) {
                     /*HSSFDataFormatter hSSFDataFormatter = new HSSFDataFormatter();
                     cellValue= hSSFDataFormatter.formatCellValue(cell);*/


						// 以下是判断数据的类型
						CellType type = cell.getCellTypeEnum();

						switch (type) {
							case NUMERIC: // 数字
								cellValue = cell.getNumericCellValue() + "";
								//转换时间格式
								if (r != 0 && c == 7) {
									Date date = dateFormat(cellValue.substring(0, cellValue.length() - 2));
//                                System.out.println(date);
									cellValue = sf.format(date);
								}
								//校验整数
								if (r != 0 && c == 6) {
									if (cellValue.contains(".")) {
										int i = cellValue.indexOf(".");
										cellValue = cellValue.substring(0, i);
									}

								}
								break;
							case STRING: // 字符串
								cellValue = cell.getStringCellValue();
								break;
							case BOOLEAN: // Boolean
								cellValue = cell.getBooleanCellValue() + "";
								break;
							case FORMULA: // 公式
								try {
									cellValue = cell.getStringCellValue();
								} catch (IllegalStateException e) {
									cellValue = String.valueOf(cell.getNumericCellValue());
								}
								break;
                       /* cellValue = cell.getCellFormula() + "";
                        break;*/
							case BLANK: // 空值
								cellValue = "";
								break;
							case _NONE: // 故障
								cellValue = "非法字符";
								break;
							default:
								cellValue = "未知类型";
								break;
						}
					}

					rowLst.add(cellValue);
				}
				/** 保存第r行的第c列 */
				dataLst.add(rowLst);
			}
			return dataLst;
		}


		//时间转换方法 传取出来的时间数字
		public static Date dateFormat(String conStart1) {
			Calendar calendar = new GregorianCalendar(1900, 0, -1);
			Date d = calendar.getTime();
			Date dd = DateUtils.addDays(d, Integer.valueOf(conStart1));
			return dd;
		}

		//判断是否空行
		private static boolean isRowEmpty(Row row) {
			for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
				Cell cell = row.getCell(c);
				if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
					return false;
			}
			return true;
		}


}