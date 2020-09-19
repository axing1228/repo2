package com.sz.fts.utils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Excel工具类
 *
 * @author wxb
 * @version [版本号, 2014-8-21]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public abstract class ExcelUtil {

	/**
	 * 生成excel文件
	 * 
	 * @param headList
	 *            excel文件头
	 * @param contentList
	 *            excel文件的内容
	 * fileName
	 *            目标文件名(如文件名为null，则自动生成一个文件名)
	 * @return 保存的文件名
	 */
	public static Workbook exportWithExcelFile(List<String> headList, List<List<String>> contentList) {
		if (headList == null || contentList == null) {
			return new SXSSFWorkbook(100);
		}
		Workbook workbook = new SXSSFWorkbook(100);// 最重要的就是使用SXSSFWorkbook，表示流的方式进行操作
		Sheet sheet = workbook.createSheet();
		CellStyle style = workbook.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		// 设置表头
		Row writeRow = sheet.createRow(0);
		for (Integer headIndex = 0; headIndex < headList.size(); headIndex++) {
			Cell cell = writeRow.createCell(headIndex);
			cell.setCellValue(headList.get(headIndex));
			cell.setCellStyle(style);

		}
		// 写入实体数据
		for (Integer rowIndex = 0; rowIndex < contentList.size(); rowIndex++) {
			Row contentRow = sheet.createRow(rowIndex);
			List<String> cList = contentList.get(rowIndex);
			for (Integer cellIndex = 0; cellIndex < cList.size(); cellIndex++) {
				Cell cell = contentRow.createCell(cellIndex);
				cell.setCellValue(cList.get(cellIndex));
				if (rowIndex.intValue() == 0) {
					style.setFillForegroundColor(HSSFColor.YELLOW.index);
					style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				} else if (rowIndex.intValue() == 1) {
					style = workbook.createCellStyle();
					style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				}
				cell.setCellStyle(style);
			}
		}
		return workbook;
	}

	/**
	 * 读取xls文件内容  *
	 * 
	 * @param column
	 * @param membercardId
	 * @return json数组列表
	 * @throws IOException
	 *             输入/输出(i/o)异常
	 * @see [类、类#方法、类#成员]
	 */
	public static List<String> readExcel(Integer column, String membercardId, String url) throws Exception {
		InputStream is = new FileInputStream(url);
		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(is);
		} catch (Exception e) {
			try {
				is = new FileInputStream(url);
				workbook = new HSSFWorkbook(is);
			} catch (Exception e2) {
				e2.printStackTrace();
				return null;
			}
		}

		// 输入json数据
		List<String> jsonList = new ArrayList<String>();
		// 循环工作表Sheet
		for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
			StringBuffer json = null;

			Sheet sheet = workbook.getSheetAt(numSheet);
			if (sheet == null) {
				break;
			}
			// 循环行Row
			for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
				String wxId = "pageImport_" + Sequence.nextId();
				json = new StringBuffer();
				json.append("[");

				Row row = sheet.getRow(rowNum);
				if (row == null) {
					break;
				}
				// 循环列Cell(动态)
				for (int cellNum = 0; cellNum < column; cellNum++) {
					Cell content = row.getCell(cellNum);
					// 拼接入库数据
					json.append("{wxId: ").append("\'" + wxId + "\',");
					json.append("membercardId: ").append("\'" + membercardId + "\',");
					json.append("key: ").append("\'" + "key" + cellNum + "\',");
					if (content == null) {
						json.append("content: ").append("\'" + "" + "\'");
					} else {
						json.append("content: ").append("\'" + getValue(content) + "\'");
					}
					if (cellNum != column - 1) {
						json.append("},");
					} else {
						json.append("}");
					}
				}
				json.append("]");
				jsonList.add(json.toString());
			}
		}

		return jsonList;
	}

	/**
	 * 得到Excel表中的值  *
	 * 
	 * @param hssfCell
	 *            Excel中的每一个格子
	 * @return Excel中每一个格子中的值
	 * @see [类、类#方法、类#成员]
	 */
	private static String getValue(Cell hssfCell) {
		if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
			// 返回布尔类型的值
			return String.valueOf(hssfCell.getBooleanCellValue());
		} else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
			// 返回数值类型的值(需要进行二次处理)
			if (HSSFDateUtil.isCellDateFormatted(hssfCell)) {
				String value = "";
				try {
					Date date = hssfCell.getDateCellValue();
					value = DateUtils.sdfhuman.format(date);
				} catch (Exception e) {
					// TODO: handle exception
				}
				return value;
			} else {
				NumberFormat f = new DecimalFormat("#");
				return f.format(hssfCell.getNumericCellValue());
			}
		} else {
			// 返回字符串类型的值
			return String.valueOf(hssfCell.getStringCellValue());
		}
	}

	public static void main(String[] args) throws Throwable {
		// Workbook workbook = new
		// SXSSFWorkbook();//最重要的就是使用SXSSFWorkbook，表示流的方式进行操作
		// Sheet sheet = workbook.createSheet();
		// int rows = 50000;
		// int cols = 20;
		// for (int row = 0; row < rows; row++)
		// {
		// Row writeRow = sheet.createRow(row);
		// for (int col = 0; col < cols; col++)
		// {
		// Cell cell = writeRow.createCell(col);
		// cell.setCellValue(row + "-" + col);
		// }
		// }
		//
		// FileOutputStream stream = new FileOutputStream("d:/x.xlsx");
		// workbook.write(stream);//最终写入文件
		// stream.close();

		try {
			String wxId = "pageImport_" + Sequence.nextId();
			String url = "F://2003.xls";
			List<String> list = readExcel(12, "20140820162151802011", url);
			for (String json : list) {
				System.out.println(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
