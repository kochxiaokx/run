package org.run.util.excel;


import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.run.date.util.DateUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;

public class ExcelUtil<T> {
    private DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
    Class<T> clazz;
    private List<String> notSave;

    public ExcelUtil(Class<T> clazz) {
        this.clazz = clazz;
        notSave = new ArrayList<>();
    }

    public Map<String,List<T>> importExcel(InputStream input)throws Exception {
        Map<String,List<T>> map = new HashMap<>();
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(input);
            int sheets = workbook.getNumberOfSheets();
            for(int k = 0 ; k < sheets ; k++) {
                int maxCol = 0;
                List<T> list = new ArrayList<T>();
                HSSFSheet sheet =	workbook.getSheetAt(k);
                int rows = sheet.getPhysicalNumberOfRows();// 得到数据的行数
                if(rows <= 0) {
                    this.notSave.add(sheet.getSheetName()+":模板错误,请用模板填写数据.\r\n\r\n");
                    continue;
                }
                if (rows > 0) {// 有数据时才处理

                    Field[] allFields = clazz.getDeclaredFields();// 得到类的所有field.
                    Map<Integer, Field> fieldsMap = new HashMap<Integer, Field>();// 定义一个map用于存放列的序号和field.

                    for (Field field : allFields) {
                        // 将有注解的field存放到map中.
                        if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
                            ExcelVOAttribute attr = field
                                    .getAnnotation(ExcelVOAttribute.class);
                            int col = getExcelCol(attr.column());// 获得列号
                            maxCol = Math.max(col, maxCol);
                            // System.out.println(col + "====" + field.getName());
                            field.setAccessible(true);// 设置类的私有字段属性可访问.
                            fieldsMap.put(col, field);
                        }
                    }

                    for (int i = 1; i < rows; i++) {// 从第2行开始取数据,默认第一行是表头.
                        HSSFRow row =	sheet.getRow(i);// 得到一行中的所有单元格对象.
                        int cellNum = maxCol;
                        if(row == null) continue;
                        T entity = null;
                        for (int j = 0; j < cellNum; j++) {
                            HSSFCell cell = row.getCell(j);
                            if (cell == null) {
                                continue;
                            }
                            CellType cellType=cell.getCellTypeEnum();
                            String c = null;
                            // if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
                            if (cellType == CellType.NUMERIC){
                                //判断是否日期类型
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    Date date = cell.getDateCellValue();
                                    c = DateUtil.format(date, "yyyy-MM-dd");
                                } else {
                                    c = decimalFormat.format(cell.getNumericCellValue());
                                }

                            } else if (cellType == CellType.BOOLEAN) {
                                c = String.valueOf(cell.getBooleanCellValue());
                            } else {
                                c = cell.getStringCellValue();
                            }
                            if (c == null || c.equals("")) {
                                continue;
                            }
                            entity = (entity == null ? clazz.newInstance() : entity);// 如果不存在实例则新建.
                            // System.out.println(cells[j].getContents());
                            Field field = fieldsMap.get(j);// 从map中得到对应列的field.
                            // 取得类型,并根据对象类型设置值.

                            if (field==null) {
                                continue;
                            }
                            Class<?> fieldType = field.getType();
                            if (String.class == fieldType) {
                                field.set(entity, String.valueOf(c));
                            } else if ((Integer.TYPE == fieldType)
                                    || (Integer.class == fieldType)) {
                                field.set(entity, Integer.parseInt(c));
                            } else if ((Long.TYPE == fieldType)
                                    || (Long.class == fieldType)) {
                                field.set(entity, Long.valueOf(c));
                            } else if ((Float.TYPE == fieldType)
                                    || (Float.class == fieldType)) {
                                field.set(entity, Float.valueOf(c));
                            } else if ((Short.TYPE == fieldType)
                                    || (Short.class == fieldType)) {
                                field.set(entity, Short.valueOf(c));
                            } else if ((Double.TYPE == fieldType)
                                    || (Double.class == fieldType)) {
                                field.set(entity, Double.valueOf(c));
                            }else if(Boolean.class == fieldType || Boolean.TYPE == fieldType) {
                                field.set(entity, str2Ble(c));
                            } else if (Character.TYPE == fieldType) {
                                if ((c != null) && (c.length() > 0)) {
                                    field.set(entity, Character
                                            .valueOf(c.charAt(0)));
                                }
                            } else if (BigDecimal.class == fieldType) {
                                field.set(entity, new BigDecimal(c));
                            } else if (Date.class == fieldType) {
                                field.set(entity, DateUtil.parse(c));
                            } else if(Timestamp.class == fieldType) {
                                Timestamp time =  new Timestamp(DateUtil.parse(c).getTime());
                                field.set(entity, time);
                            }
                        }
                        if (entity != null) {
                            list.add(entity);
                        }

                    }
                }
                map.put(sheet.getSheetName(), list);
            }


        }finally {
            try {
                if(input != null) {
                    input.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 对list数据源将其里面的数据导入到excel表单
     *
     * @param sheetName
     *            工作表的名称
     * @param sheetSize
     *            每个sheet中数据的行数,此数值必须小于65536
     * @param output
     *            java输出流
     */
    public boolean exportExcel(List<T> list, String sheetName, int sheetSize,
                               OutputStream output) {

        Field[] allFields = clazz.getDeclaredFields();// 得到所有定义字段
        List<Field> fields = new ArrayList<>();
        // 得到所有field并存放到一个list中.
        for (Field field : allFields) {
            if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
                fields.add(field);
            }
        }

        HSSFWorkbook workbook = new HSSFWorkbook();// 产生工作薄对象

        // excel2003中每个sheet中最多有65536行,为避免产生错误所以加这个逻辑.
        if (sheetSize > 65536 || sheetSize < 1) {
            sheetSize = 65536;
        }
        double sheetNo = Math.ceil(list.size() / sheetSize);// 取出一共有多少个sheet.
        for (int index = 0; index <= sheetNo; index++) {
            HSSFSheet sheet = workbook.createSheet();// 产生工作表对象
            workbook.setSheetName(index, sheetName + index);// 设置工作表的名称.
            HSSFRow row;
            HSSFCell cell;// 产生单元格

            row = sheet.createRow(0);// 产生一行
            // 写入各个字段的列头名称
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                ExcelVOAttribute attr = field
                        .getAnnotation(ExcelVOAttribute.class);
                int col = getExcelCol(attr.column());// 获得列号
                cell = row.createCell(col);// 创建列
                cell.setCellType(CellType.STRING);// 设置列中写入内容为String类型
                cell.setCellValue(attr.name());// 写入列名

                // 如果设置了提示信息则鼠标放上去提示.
                if (!attr.prompt().trim().equals("")) {
                    setHSSFPrompt(sheet, "", attr.prompt(), 1, 100, col, col);// 这里默认设了2-101列提示.
                }
                // 如果设置了combo属性则本列只能选择不能输入
                if (attr.combo().length > 0) {
                    setHSSFValidation(sheet, attr.combo(), 1, 100, col, col);// 这里默认设了2-101列只能选择不能输入.
                }
            }

            int startNo = index * sheetSize;
            int endNo = Math.min(startNo + sheetSize, list.size());
            // 写入各条记录,每条记录对应excel表中的一行
            for (int i = startNo; i < endNo; i++) {
                row = sheet.createRow(i + 1 - startNo);
                T vo = (T) list.get(i); // 得到导出对象.
                for (int j = 0; j < fields.size(); j++) {
                    Field field = fields.get(j);// 获得field.
                    field.setAccessible(true);// 设置实体类私有属性可访问
                    ExcelVOAttribute attr = field
                            .getAnnotation(ExcelVOAttribute.class);
                    try {
                        // 根据ExcelVOAttribute中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
                        if (attr.isExport()) {
                            cell = row.createCell(getExcelCol(attr.column()));// 创建cell
                            cell.setCellType(CellType.STRING);
                            cell.setCellValue(field.get(vo) == null ? ""
                                    : String.valueOf(field.get(vo)));// 如果数据存在就填入,不存在填入空格.
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        try {
            output.flush();
            workbook.write(output);
            output.close();
            workbook.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Output is closed ");
            return false;
        }

    }

    /**
     * 将EXCEL中A,B,C,D,E列映射成0,1,2,3
     *
     * @param col
     */
    public static int getExcelCol(String col) {
        col = col.toUpperCase();
        // 从-1开始计算,字母重1开始运算。这种总数下来算数正好相同。
        int count = -1;
        char[] cs = col.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            count += (cs[i] - 64) * Math.pow(26, cs.length - 1 - i);
        }
        return count;
    }

    /**
     * 设置单元格上提示
     *
     * @param sheet
     *            要设置的sheet.
     * @param promptTitle
     *            标题
     * @param promptContent
     *            内容
     * @param firstRow
     *            开始行
     * @param endRow
     *            结束行
     * @param firstCol
     *            开始列
     * @param endCol
     *            结束列
     * @return 设置好的sheet.
     */
    public static HSSFSheet setHSSFPrompt(HSSFSheet sheet, String promptTitle,
                                          String promptContent, int firstRow, int endRow, int firstCol,
                                          int endCol) {
        // 构造constraint对象
        DVConstraint constraint = DVConstraint
                .createCustomFormulaConstraint("DD1");
        // 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,
                endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation data_validation_view = new HSSFDataValidation(
                regions, constraint);
        data_validation_view.createPromptBox(promptTitle, promptContent);
        sheet.addValidationData(data_validation_view);
        return sheet;
    }

    /**
     * 设置某些列的值只能输入预制的数据,显示下拉框.
     *
     * @param sheet
     *            要设置的sheet.
     * @param textlist
     *            下拉框显示的内容
     * @param firstRow
     *            开始行
     * @param endRow
     *            结束行
     * @param firstCol
     *            开始列
     * @param endCol
     *            结束列
     * @return 设置好的sheet.
     */
    public static HSSFSheet setHSSFValidation(HSSFSheet sheet,
                                              String[] textlist, int firstRow, int endRow, int firstCol,
                                              int endCol) {
        // 加载下拉列表内容
        DVConstraint constraint = DVConstraint
                .createExplicitListConstraint(textlist);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,
                endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation data_validation_list = new HSSFDataValidation(
                regions, constraint);
        sheet.addValidationData(data_validation_list);
        return sheet;
    }

    public List<String> getNotSave() {
        return notSave;
    }

    public void setNotSave(List<String> notSave) {
        this.notSave = notSave;
    }

    public Boolean str2Ble(String str) {
        str = str.trim();
        if("Y".equals(str) || "1".equals(str) || "true".equals(str)) {
            return true;
        }else if(str == null) {
            return null;
        }else if("N".equals(str) || "0".equals(str) || "false".equals(str)){
            return false;
        }
        return null;
    }
}