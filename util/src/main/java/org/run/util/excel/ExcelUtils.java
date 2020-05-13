package org.run.util.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.run.date.util.DateUtil;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelUtils<T> {
    private DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
    Class<T> clazz;
    private List<String> errorMessage;
    public ExcelUtils(Class<T> clazz) {
        this.clazz = clazz;
        errorMessage = new ArrayList<>(10);
    }

    public Map<String,List<T>> importExcel( InputStream input) {
        Map<String,List<T>> resultMap = new HashMap<>();

        try {

            //Workbook book = Workbook.getWorkbook(input);
            // HSSFWorkbook workbook = new HSSFWorkbook(input);
            XSSFWorkbook workbook = new XSSFWorkbook(input);
            int sheets = 	workbook.getNumberOfSheets();
            for(int k = 0; k < sheets ; k++) {
                //boolean flag = false;  //标志出入的sheet模板有没有严格按照模板来写
                int maxCol = 0;
                List<T> list = new ArrayList<T>();
                Map<String, String>  maplist=null;
                String tem = null;
                //  HSSFSheet sheet = workbook.getSheetAt(k);
                XSSFSheet sheet =	workbook.getSheetAt(k);
                maplist = getPictures2(sheet);
                int rows = sheet.getPhysicalNumberOfRows();// 得到数据的行数
                if(rows <= 0) {
                    this.errorMessage.add(sheet.getSheetName()+":模板错误,请用模板填写数据\r\n\r\n");
                    continue;
                }
                if (rows > 0) {// 有数据时才处理
                    // Field[] allFields = clazz.getDeclaredFields();// 得到类的所有field.
                    List<Field> allFields = getMappedFiled(clazz, null);

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

                    checkSheetHead(sheet.getSheetName(),sheet.getRow(0), maxCol); //检查表头



                    for (int i = 1; i < rows; i++) {// 从第2行开始取数据,默认第一行是表头
                        tem = String.valueOf(i);
                        //    HSSFRow row = sheet.getRow(i);
                        XSSFRow row =	sheet.getRow(i);
                        // int cellNum = row.getPhysicalNumberOfCells();
                        // int cellNum = row.getLastCellNum();
                        int cellNum = maxCol;
                        if(row == null) continue;  // int rows = sheet.getPhysicalNumberOfRows(); 会或得不准确的行数,如果为空就直接跳过
                        T entity = null;
                        for (int j = 0; j <=cellNum; j++) {

                            XSSFCell cell = row.getCell(j);
                            if (cell == null && j!=0) {
                                continue;
                            }
                            // int cellType = cell.getCellType();
                            //cell.getCellTypeEnum().getCode();
                            CellType cellType=cell.getCellTypeEnum();
                            String c = null;
                            // if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
                            if (cellType == CellType.NUMERIC){
                                //判断是否日期类型
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    Date date = cell.getDateCellValue();
                                    c = DateUtil.format(date);
                                } else {
                                    c = decimalFormat.format(cell.getNumericCellValue());
                                }

                            } else if (cellType == CellType.BOOLEAN) {
                                c = String.valueOf(cell.getBooleanCellValue());
                            } else {
                                c = cell.getStringCellValue();
                            }
                            if (c == null || c.equals("")) {
                                if(j==0 && (c==null || c.equals(""))) {
                                    if(!list.isEmpty()) {
                                        Field field = fieldsMap.get(j);
                                        PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), clazz);
                                        T t =	list.get(list.size()-1 < 0 ? list.size():list.size()-1);
                                        c =	(String)descriptor.getReadMethod().invoke(t);
                                    }else {
                                        c="";
                                    }
                                }else {
                                    continue;
                                }

                            }
                            entity = (entity == null ? clazz.newInstance() : entity);
                            Field field = fieldsMap.get(j);
                            if (field==null) {
                                continue;
                            }
                            // 取得类型,并根据对象类型设置值.
                            Class fieldType = field.getType();
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
                            } else if (Character.TYPE == fieldType) {
                                if ((c != null) && (c.length() > 0)) {
                                    field.set(entity, Character
                                            .valueOf(c.charAt(0)));
                                }
                            } else if (BigDecimal.class == fieldType) {
                                field.set(entity, new BigDecimal(c));
                            } else if (Date.class == fieldType) {
                                field.set(entity, DateUtil.parse(c));
                            }

                        }
                        if (entity != null) {
                            //设置图片
                            //直接写死
                            PropertyDescriptor image = new PropertyDescriptor("imagePath", clazz);
                            image.getWriteMethod().invoke(entity,maplist.get(tem));
	                    	/*
	                    	PropertyDescriptor name = new PropertyDescriptor("name", clazz);
	                    	Object nameVal =	name.getReadMethod().invoke(entity);
	                    	if(nameVal == null || "".equals(nameVal)) {  //如果没有值用上一个对象的字段
	                    		System.out.println(entity);
	                    	}*/
                            list.add(entity);
                        }
                    }
                }
                resultMap.put(sheet.getSheetName(),list);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultMap;
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
    public static Map<String, String> getPictures1 (HSSFSheet sheet) throws IOException {
        Map<String, String> map = new HashMap<>();
        List<HSSFShape> list = sheet.getDrawingPatriarch().getChildren();
        for (HSSFShape shape : list) {
            if (shape instanceof HSSFPicture) {
                HSSFPicture picture = (HSSFPicture) shape;
                HSSFClientAnchor cAnchor = (HSSFClientAnchor) picture.getAnchor();
                PictureData pdata = picture.getPictureData();
                // String key = cAnchor.getRow1() + "-" + cAnchor.getCol1(); // 行号-列号
                String key = String.valueOf(cAnchor.getRow1());
                byte[] pcbyte = 	pdata.getData();
                //保存照片的逻辑
              /*  FileServiceImpl f = new FileServiceImpl();
                InputStream is = new ByteArrayInputStream(pcbyte);
                String name = UUID.randomUUID().toString().replaceAll("-", "")+".png";
                f.save(is, "png", name);
                map.put(key, ConstantUtil.DIR+name);*/
            }
        }
        return map;
    }

    public static Map<String, String> getPictures2 (XSSFSheet sheet) throws IOException {
        Map<String, String> map = new HashMap<>();
        List<POIXMLDocumentPart> list = sheet.getRelations();
        for (POIXMLDocumentPart part : list) {
            if (part instanceof XSSFDrawing) {
                XSSFDrawing drawing = (XSSFDrawing) part;
                List<XSSFShape> shapes = drawing.getShapes();
                for (XSSFShape shape : shapes) {
                    XSSFPicture picture = (XSSFPicture) shape;
                    XSSFClientAnchor anchor = picture.getPreferredSize();
                    CTMarker marker = anchor.getFrom();
                    String key = String.valueOf(marker.getRow());
                    byte[] pcbyte = 	picture.getPictureData().getData();
                    // 保存照片的逻辑
                    /*FileServiceImpl f = new FileServiceImpl();
                    InputStream is = new ByteArrayInputStream(pcbyte);
                    String name = UUID.randomUUID().toString().replaceAll("-", "")+".png";
                    f.save(is, "png", name);
                    map.put(key, ConstantUtil.DIR+name);*/
                }
            }
        }
        return map;
    }

    /**
     * 得到实体类所有通过注解映射了数据表的字段
     *
     * @param fields
     * @return
     */
    @SuppressWarnings("rawtypes")
    private List<Field> getMappedFiled(Class clazz, List<Field> fields) {
        if (fields == null) {
            fields = new ArrayList<Field>();
        }

        Field[] allFields = clazz.getDeclaredFields();// 得到所有定义字段
        // 得到所有field并存放到一个list中.
        for (Field field : allFields) {
            if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
                fields.add(field);
            }
        }
        if (clazz.getSuperclass() != null
                && !clazz.getSuperclass().equals(Object.class)) {
            getMappedFiled(clazz.getSuperclass(), fields);
        }

        return fields;
    }

    private boolean checkSheetHead(String sheetName,XSSFRow row,int maxCol) {
        String[] sheetHead = {
                "Style款式","Style#款号","Picture图片","Machine机型","diameter筒径","needles针数","M/G","yarninfo纱线信息","Location位置","RFID编码"
        };
        if(row == null) {
            this.errorMessage.add(sheetName+":模板错误,请用模板填写数据\r\n\r\n");
            return false;
        }
        for(int i = 0; i < maxCol ; i++) {
            XSSFCell cell =	row.getCell(i);
            if(cell == null) break;
            String cellValue = cell.getStringCellValue();
            if(cellValue != null) {  //去掉换行符
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(cellValue);
                cellValue = m.replaceAll("");
            }
            //System.out.println(cellValue+" "+sheetHead[i]+"-->"+!cellValue.equals(sheetHead[i]));
            if(!cellValue.equals(sheetHead[i])) {
                this.errorMessage.add(sheetName+":模板错误,请用模板填写数据\r\n\r\n");
                return false;
            }
        }
        return true;

    }
    public static void main(String[] args) {

        int i=	getExcelCol("A");
        System.out.println(i);
    }

    public List<String> getErrorMessage() {
        return this.errorMessage;
    }
}