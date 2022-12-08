package com.utils.lingfe;

import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Atest3Csv {

    static String path="C:\\Users\\Administrator\\Desktop\\数据库文件\\镇江市数据(1)\\镇江市数据\\";
    static String pathCity="镇江站点-分区.xls";
    static String pathData="data.csv";
    static String suffix=".xlsx";

    static String pathModle="";
    static String pathOk="";

    public static void main(String[] args) throws FileNotFoundException {

        //第一步，xuhao
        for (int j=0;j<9;j++){
            //动态模板路径
            //int j=0;
            pathModle=new StringBuffer(path).append(j+1).append(suffix).toString();
            System.out.println("动态模板路径:"+pathModle);

            //读-hutool
            ExcelReader reader = ExcelUtil.getReader(path + pathCity, j);
            //拼接另存为路径。
            String  sheetName = reader.getSheet().getSheetName();
            pathOk=new StringBuffer(path).append(sheetName).append("-核查表").append(suffix).toString();

            //for处理资产
            List<Object> list= Collections.singletonList(reader.read(2));
            List<Object> listString= (List<Object>) list.get(0);
            for (int i=0;i<listString.size();i++) {
                //for (int i=0;i<4;i++) {
                List<String> item= (List<String>) listString.get(i);
                ZipSecureFile.setMinInflateRatio(-1.0d);//增加使用内存
                System.out.println("资产编号item:"+JSON.toJSONString(item));
                //资产item-第i个工作表
                extracted(item,i);
            }

            //关闭释放
            reader.close();

            //删除多余工作表
            int i=0;
            while (true){
                try {
                    //加载-hutool
                    System.out.println("已经处理的文件路径:"+pathOk);
                    ExcelWriter writer= ExcelUtil.getWriter(pathOk);
                    Sheet s=writer.getSheets().get(i);
                    String sheet1 = s.getSheetName();

                    //验证工作表
                    Boolean bl= StringUtils.isNumber(sheet1);
                    System.out.println(i+"工作本-"+sheet1+",是否为数字:"+bl);
                    if(!bl){
                        //删除-工作表
                        System.out.println("删除前-sheets="+writer.getSheets().size());
                        writer.getWorkbook().removeSheetAt(i);
                        System.out.println("book-删除后-sheets="+writer.getSheets().size());
                        i=i-1;

                        //检测完成退出while
                        if("sheet1".equals(sheet1)){
                            System.out.println("检测到已完成删除-退出while***********");
                            break;
                        }
                    }else{
                        i++;
                    }

                    // 关闭writer，释放内存
                    writer.flush();
                    writer.setDestFile(new File(pathOk));
                    writer.close();
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    /** 设置序号、编码、创建行、创建副本、筛选数据、填充数据 */
    private static void extracted(List<String> item, int itt) {
        //得到序号和编号
        Object num=item.get(0);
        Object id=item.get(1);

        //得到数据
        CsvReader reader = CsvUtil.getReader();
        //根据特定的编码方式读取File的内容
        CsvData csvData = reader.read(new File(path+pathData), CharsetUtil.CHARSET_GBK);
        //读取文件中的所有的行数据
        List<CsvRow> rows = csvData.getRows();

        //循环提取数据
        List<Object> listRow=new ArrayList<>();
        for (int i=0;i<rows.size();i++) {
            List<String> row= rows.get(i).getRawList();
            if(id.equals(row.get(0))){
                listRow.add(row);
                System.out.print("提取数据:");
                System.out.println(JSON.toJSONString(row));
            }
        }

        //初始化写入
        ExcelWriter writer=ExcelUtil.getWriter(pathModle);
        writer.setSheet(itt);

        //设置序号-编码
        writer.renameSheet(itt+1+"");
        writer.writeCellValue(1,1, num);
        writer.writeCellValue(2,1, id);

        //提前创建好样式，如果你在for循环内设置样式的话，样式对象是有限的会导致有些样式缺失但不报错的问题。一定要注意！！！！
        CellStyle styleSet=writer.getHeadCellStyle();
        //设置背景颜色
        styleSet.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        styleSet.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //单元格内容自动换行
        styleSet.setWrapText(true);
        //设置水平对齐的样式为居中对齐;
        styleSet.setAlignment(HorizontalAlignment.CENTER);
        //设置垂直对齐的样式为居中对齐;
        styleSet.setVerticalAlignment(VerticalAlignment.CENTER);

        //设置编号、增加行、填充数据
        for(int j=0;j<listRow.size();j++){
            List<String> rowData=(List<String>) listRow.get(j);
            System.out.print("载入数据:");
            System.out.println(JSON.toJSONString(rowData));
            int y=4+j;
            writer.getSheet().createRow(y);
            writer.writeCellValue(0,y,j+1);
            for(int i=0;i<25;i++){
                int x=(i+1);
                Object value="";
                try{value=rowData.get(i+2);}catch (Exception e){}
                writer.writeCellValue(x,y,value);
                writer.setStyle(styleSet,x,y);
            }
            writer.getSheet().getRow(y).setHeight((short) 800);
        }

        // 关闭writer，释放内存
        writer.flush();
        writer.setDestFile(new File(pathOk));
        writer.close();
    }


    /**
     * 复制sheet到另一个excel文件中
     *
     * @param newWorkbook 新文件工作簿
     * @param newSheet    新文件sheet
     * @param oldSheet    老文件sheet
     */
    private static void copySheet(Workbook newWorkbook, Sheet newSheet, Sheet oldSheet) {

        //合并单元格
        int numMergedRegions = oldSheet.getNumMergedRegions();
        for (int i = 0; i < numMergedRegions; i++) {
            CellRangeAddress mergedRegion = oldSheet.getMergedRegion(i);
//            int startRowIndex = 0;
//            int endRowIndex = 1;
//            int startColIndex = 0;
//            int endColIndex = 1;
//            List<CellRangeAddress> cellRangeAddressList = oldSheet.getMergedRegions();
//            List<CellRangeAddress> haveCellRangeAddressList = CollUtil.isNotEmpty(cellRangeAddressList) ?
//                    cellRangeAddressList.stream().filter(x ->
//                            CollUtil.isIntersection(x.getFirstColumn(), x.getLastColumn(), startColIndex, endColIndex)
//                            && ScopeUtil.isIntersection(x.getFirstRow(), x.getLastRow(), startRowIndex, endRowIndex))
//                            .collect(Collectors.toList()) : new ArrayList<>();
//            if (CollUtil.isEmpty(haveCellRangeAddressList)) {
//                oldSheet.addMergedRegion(new CellRangeAddress(startRowIndex, endRowIndex, startColIndex, endColIndex));
//            }
            newSheet.addMergedRegion(mergedRegion);
        }
        //增加列宽
        int physicalNumberOfCells = oldSheet.getRow(0).getPhysicalNumberOfCells();
        for (int i = 0; i < physicalNumberOfCells; i++) {
            newSheet.setColumnWidth(i, 256 * 20);
        }

        //最大获取行数
        int maxRowSize = oldSheet.getPhysicalNumberOfRows();
        for (int i = 0; i < maxRowSize; i++) {
            Row newRow = newSheet.createRow(i);
            Row oldRow = oldSheet.getRow(i);


            //获取当前行,最大列数
            int maxColSize = oldRow.getPhysicalNumberOfCells();
            for (int j = 0; j < maxColSize; j++) {
                Cell newCell = newRow.createCell(j);
                Cell oldCell = oldRow.getCell(j);
                if (oldCell == null) {
                    continue;
                }
                CellType cellType = oldCell.getCellType();
                switch (cellType) {
                    case NUMERIC:
                        newCell.setCellValue(oldCell.getNumericCellValue());
                        break;
                    case STRING:
                        newCell.setCellValue(oldCell.getStringCellValue());
                        break;
                    case BLANK:
                        break;
                    case ERROR:
                        newCell.setCellValue(oldCell.getErrorCellValue());
                        break;
                    case BOOLEAN:
                        newCell.setCellValue(oldCell.getBooleanCellValue());
                        break;
                    case FORMULA:
                        newCell.setCellFormula(oldCell.getCellFormula());
                        break;
                }

                //直接copy原cell的样式,定义在遍历行的地方,定义在外面可能出现样式渲染错误
                CellStyle cellStyle = newWorkbook.createCellStyle();
                cellStyle.cloneStyleFrom(oldCell.getCellStyle());
                newCell.setCellStyle(cellStyle);
            }
        }
    }

}
