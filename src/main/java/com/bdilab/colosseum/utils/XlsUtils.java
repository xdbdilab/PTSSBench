package com.bdilab.colosseum.utils;

import com.bdilab.colosseum.vo.ParameterVo;
import com.bdilab.colosseum.vo.PerformanceVo;
import com.bdilab.colosseum.vo.XlsRead;
import com.csvreader.CsvReader;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.rmi.ServerException;
import java.util.*;

/**
 * @author SunRen
 * @version 1.0
 * @date 2021/1/4 14:35
 */
public class XlsUtils {

    public static int templateParamUploadFileTitleSize = Integer.parseInt(ApplicationProperties.getInstance().getValue("file.template.param-upload.title-size"));
    public static int templatePerformanceUploadFileTitleSize = Integer.parseInt(ApplicationProperties.getInstance().getValue("file.template.performance-upload.title-size"));

    /**
     * 读取xls文件中的数据
     * @param templatePath
     * @return
     */
    public static XlsRead parseReadxls(String templatePath)throws Exception {
        XlsRead result = new XlsRead();

        Workbook workbook = null;
        FileInputStream fis = null;
        File template = new File(templatePath);
        try {
            System.out.println(templatePath);
            System.out.println(template);
            fis = new FileInputStream(template);

            if (templatePath.endsWith("xls")) {
                workbook = new HSSFWorkbook(fis);//Excel 2003
            } else if (templatePath.endsWith("xlsx")) {
                workbook = new XSSFWorkbook(fis);//Excel 2007
            }
            Sheet sheet = workbook.getSheetAt(0);
            // 存放一页的结果
            List<Map<String, Object>> sheetList = new ArrayList<>();
            // 记录表头的位置
            Map<Integer, String> titleMap = new HashMap<>(sheet.getPhysicalNumberOfRows());
            for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
                // 获取第j行数据(从第一行开始)
                Row row = sheet.getRow(j);
                if (Objects.isNull(row)) {
                    // 如果整行为空，则跳过
                    continue;
                }
                // 存放一行的数据
                Map<String, Object> rowMap = new HashMap<>(row.getLastCellNum());
                for (int k = 0; k < (row.getLastCellNum() > titleMap.size() ? row.getLastCellNum() : titleMap.size()); k++) {
                    if (0 == j) {
                        if (Objects.nonNull(row.getCell(k))) {
                            // 将每一页的第一行作为表头记录
                            titleMap.put(k, row.getCell(k).toString());
                        }
                    } else {
                        // 只记录有表头的数据
                        if (Objects.nonNull(titleMap.get(k))) {
                            Object cellValue = getCellValue(row.getCell(k));
                            rowMap.put(titleMap.get(k), Objects.nonNull(cellValue) ? cellValue : "");
                        }
                    }
                }
                if (!rowMap.isEmpty()) {
                    sheetList.add(rowMap);
                }
            }
            result.setTitleMap(titleMap);
            if (CollectionUtils.isNotEmpty(sheetList)) {
                result.setDataList(sheetList);
            }
        } finally {
            // 关闭资源
            workbook.close();
            fis.close();
        }
        return result;
    }

    /**
     * 根据单元格的类型获取相应的值
     *
     * @param cell
     * @return
     */
    public static Object getCellValue(Cell cell) {
        Object cellValue = null;
        if (Objects.nonNull(cell) && cell.getCellType() == CellType.NUMERIC) {

            // 数值型
            // poi读取整数会自动转成小数，这里对整数进行还原，小数不会做处理
            long longValue = Math.round(cell.getNumericCellValue());
            if (Double.parseDouble(longValue + ".0") == cell.getNumericCellValue()) {
                cellValue = longValue;
            }
            cellValue = cell.getNumericCellValue();
        }else if (Objects.nonNull(cell) && cell.getCellType() == CellType.FORMULA) {
            // 公式型
            // 公式计算的值不会转成小数，这里数值获取失败后会获取字符
            try {
                cellValue = cell.getNumericCellValue();
            } catch (Exception e) {
                cellValue = cell.getStringCellValue();
            }
        } else if (Objects.nonNull(cell) && cell.getCellType() == CellType.STRING) {
            cellValue =cell.getStringCellValue();
        }else if (Objects.isNull(cell)||(cell.getCellType() == CellType.BLANK)){
            cellValue = null;
        } else{
            // 其他类型不作处理
            cellValue = cell;
        }
        return cellValue;
    }

    public static List<List<String>> getCellFromCSV(String path) {
        List<String[]> csvReader = new ArrayList<>();
        try {
            // 创建csv读对象
            CsvReader reader = new CsvReader(path, ',' , Charset.forName("UTF-8"));

            while (reader.readRecord()) {
                csvReader.add(reader.getValues());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<List<String>> result = new ArrayList<List<String>>();
        // 遍历读取csv文件
        for (int row = 0; row < csvReader.size(); row++) {
            result.add(Arrays.asList(csvReader.get(row)));
        }
        return result;
    }

    /**
     * 读取xls文件
     * @param path
     * @return
     */
    public static Map<String, List<Object>> getCellFromXls(String path) {
        XlsRead xlsRead = null;
        try {
            xlsRead = parseReadxls(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, List<Object>> resultMap = new HashMap<>();
        // 获取标题行
        Map<Integer, String> titleMap = xlsRead.getTitleMap();
        // 获取数据行
        List<Map<String, Object>> dataList = xlsRead.getDataList();
        // 遍历数据行
        for (Map<String, Object> rowMap : dataList) {
            for (Integer i = 0; i < titleMap.size(); i++) {
                String title = titleMap.get(i);
                if (resultMap.containsKey(title)) {
                    resultMap.get(title).add(rowMap.get(title));
                } else {
                    List<Object> value = new ArrayList<>();
                    value.add(rowMap.get(title));
                    resultMap.put(title,value);
                }
            }
        }
        return resultMap;
    }

    public static List<ParameterVo> getParamsFromXls(String path) throws ServerException {
        XlsRead xlsRead = null;
        try {
            xlsRead = parseReadxls(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<Integer, String> titleMap = xlsRead.getTitleMap();
        List<Map<String, Object>> dataList = xlsRead.getDataList();
        List<ParameterVo> parameterVos = new ArrayList<>();
        int titleSize = titleMap.size();
        if (titleSize != templateParamUploadFileTitleSize) {
            throw new ServerException("导入文件表头格式与模板不一致");
        }
        if (dataList == null) {
            throw new ServerException("导入文件未填写内容");
        }

        for (Map<String, Object> rowMap : dataList) {
            ParameterVo parameterVo = new ParameterVo();
            for (Integer i = 0; i < titleMap.size(); i++) {
                String title = titleMap.get(i);
                switch (title) {
                    case "name":
                        parameterVo.setName((String) rowMap.get(title));
                        break;
                    case "type":
                        parameterVo.setType((String) rowMap.get(title));
                        break;
                    case "minValue":
                        if (rowMap.get(title).equals("")){
                            parameterVo.setMinValue(null);
                        }else {
                            parameterVo.setMinValue(rowMap.get(title));
                        }
                        break;
                    case "maxValue":
                        if (rowMap.get(title).equals("")){
                            parameterVo.setMaxValue(null);
                        }else {
                            parameterVo.setMaxValue(rowMap.get(title));
                        }
                        break;
                    case "validValue":
                        String validValueString = (String) rowMap.get(title);
                        if (StringUtils.isEmpty(validValueString)){
                            parameterVo.setValidValue(null);
                        }else {
                            parameterVo.setValidValue(validValueString);
                        }
                        break;
                    case "defaultValue":
                        String defaultValueString = rowMap.get(title).toString();
                        if (StringUtils.isEmpty(defaultValueString)){
                            parameterVo.setDefaultValue(null);
                        }else {
                            parameterVo.setDefaultValue(defaultValueString);
                        }
                        break;
                    default:
                        break;
                }
            }
            parameterVos.add(parameterVo);
        }
        return parameterVos;
    }

    public static List<PerformanceVo> getPerformanceFromXls(String path) throws ServerException {
        XlsRead xlsRead = null;
        try {
            xlsRead = parseReadxls(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<Integer, String> titleMap = xlsRead.getTitleMap();
        List<Map<String, Object>> dataList = xlsRead.getDataList();
        List<PerformanceVo> performanceVos = new ArrayList<>();
        int titleSize = titleMap.size();
        if (titleSize != templatePerformanceUploadFileTitleSize) {
            throw new ServerException("导入文件表头格式与模板不一致");
        }
        if (dataList == null) {
            throw new ServerException("导入文件未填写内容");
        }

        for (Map<String, Object> rowMap : dataList) {
            PerformanceVo performanceVo = new PerformanceVo();
            for (Integer i = 0; i < titleMap.size(); i++) {
                String title = titleMap.get(i);
                switch (title) {
                    case "system_performance":
                        performanceVo.setSystemPerformance((String) rowMap.get(title));
                        break;
                    case "actual_performance":
                        performanceVo.setActualPerformance((String) rowMap.get(title));
                        break;
                    case "performanceDesc":
                        if (rowMap.get(title).equals("")){
                            performanceVo.setPerformanceDesc(null);
                        }else{
                            performanceVo.setPerformanceDesc((String) rowMap.get(title));
                        }
                        break;
                    case "minOrMax":
                        performanceVo.setMinOrMax((String) rowMap.get(title));
                        break;
                    default:
                        break;
                }
            }
            performanceVos.add(performanceVo);
        }
        return performanceVos;
    }
    /**
     * 根据excel路径和系统performance获取映射在基准测试结果文件中的performance名称
     * @param performanceFilePath
     * @param systemPerformance
     * @return
     * @throws Exception
     */
    public static String readExcel(String performanceFilePath, String systemPerformance, Integer rowNumber) throws Exception {
        String performance = "";
        // 检查文件
        File file = new File(performanceFilePath);
        checkFile(file);
        // 获取Wrokbook对象
        Workbook workbook = getWorkBook(file);
        if (workbook != null) {
            for(int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //循环所有行
                for(int rowNum = firstRowNum;rowNum <= lastRowNum;rowNum++){
                    // 获取当前行
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    // 获取当前行的第一列
                    Cell cell = row.getCell(0);
                    // 找到系统performance
                    if (systemPerformance.equals(getCellValue(cell))) {
                        Cell cell1 = row.getCell(rowNumber);
                        return getCellValue(cell1).toString();
                    }
                }
            }
        }
        return performance;
    }

    private static Workbook getWorkBook(File file) throws IOException {
        Workbook workbook = null;
        String fileName = file.getName();

        FileInputStream fis = new FileInputStream(file);
        if (fileName.endsWith("xls")) {
            workbook = new HSSFWorkbook(fis);//Excel 2003
        } else if (fileName.endsWith("xlsx")) {
            workbook = new XSSFWorkbook(fis);//Excel 2007
        }
        return workbook;
    }

    /**
     * 检查文件是否存在+是否为excel文件
     * @param file
     * @throws IOException
     */
    private static void checkFile(File file) throws IOException {
        // 判断文件是否存在
        if (null == file) {
            throw new FileNotFoundException("文件不存在！");
        }
        // 获取文件名
        String fileName = file.getName();
        if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
            throw new IOException(fileName + "不是excel文件");
        }
    }
}
