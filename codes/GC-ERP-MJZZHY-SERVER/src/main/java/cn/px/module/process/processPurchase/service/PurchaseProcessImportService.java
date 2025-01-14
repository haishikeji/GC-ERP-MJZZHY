package cn.px.module.process.processPurchase.service;

import cn.px.module.process.processPurchase.bean.BomPartPurchase;
import cn.px.module.process.processPurchase.bean.BomPartPurchaseDetail;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/***
 * 2023-2-14
 * 工艺公式页面，采购工艺单的导入功能
 */
@Service
public class PurchaseProcessImportService {

    // 2022-6-21 追加，用于返回错误信息
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param fileUrl
     * @param sheetName
     */
    public void analysisExcelFile(String fileUrl, String sheetName) {

        this.errorMessage = "";

        // 读取 excel文件，返回行列结构
        ExcelTable excelTable = this.step1(fileUrl, sheetName);

        // 解析内部数据结构
        List<SplitTabel> splitTabels = this.step2(excelTable);

        // 插入吧
        this.step3(splitTabels);
    }

    /**
     * 无脑读取数据
     *
     * @param fileUrl   数据在服务器上的路径
     * @param sheetName 要读取的sheetName
     * @return 返回的结构表
     */
    private ExcelTable step1(String fileUrl, String sheetName) {
        ExcelTable excelTable = new ExcelTable();
        try {

            Workbook wb = WorkbookFactory.create(new File(fileUrl));

            if (wb == null) {
                throw new RuntimeException("未能打开excel!");
            }

            //读取
            Sheet sheet = wb.getSheet(sheetName);//根据名称读取
            if (sheet == null) {
                throw new RuntimeException("Excel不存在sheet!");
            }

            int rowNum = sheet.getLastRowNum();
            int columnNum = 0;
            int rowIndex = 0;
            int colIndex = 0;
            Row row = null;
            Cell cell = null;
            String cellValue = "";
            List<ExcelRows> excelRows = new ArrayList<ExcelRows>();
            ExcelRows excelRow;
            ExcelCell excelCol;
            // 没错就是 <=
            for (rowIndex = 0; rowIndex <= rowNum; rowIndex++) {
                row = sheet.getRow(rowIndex);//获取行
                if (row == null) {
                    continue;
                }
                columnNum = row.getLastCellNum();
                excelRow = new ExcelRows();
                excelRow.row = rowIndex;
                excelRow.cells = new ArrayList<>();
                for (colIndex = 0; colIndex < columnNum; colIndex++) {
                    cell = row.getCell(colIndex);//获取第一行
                    if (cell == null) {
                        continue;
                    }
                    cellValue = getValue(cell);
                    showCellValue(rowIndex, colIndex, cellValue);

                    excelCol = new ExcelCell();
                    excelCol.row = rowIndex;
                    excelCol.col = colIndex;
                    excelCol.val = cellValue.trim();
                    excelRow.cells.add(excelCol);
                }
                excelRows.add(excelRow);
            }
            excelTable.rows = excelRows;
        } catch (Exception ex) {
            //  throw  Ex
            System.out.println(ex.getMessage());
        }
        return excelTable;
    }

    /**
     * 很明显，根据cell的类型获取cell的值
     *
     * @param hssfCell 得到的cell
     * @return 返回内容
     */
    private String getValue(Cell hssfCell) {
        if (hssfCell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }

    private void showCellValue(int row, int col, String val) {
        System.out.println("row:" + row + ",col:" + col + ",val:" + val);
    }

    private class ExcelCell {
        public int row;
        public int col;
        public String val;
    }

    private class ExcelRows {
        public int row;
        public List<ExcelCell> cells;
    }

    private class ExcelTable {
        public List<ExcelRows> rows;
    }


    private class SplitStandItem {
        // 修应该
        public String index;

        // 设计代号
        public String code;

        // 名称
        public String name;

        // 规格
        public String thick;


        public String width;

        public String length;

        public String material;

        // 数量
        public String number;

        // 备注
        public String remark;
    }

    private class SplitStandItem2 {
        public String to;
        public String from;
    }

    private class SplitTabel {

        public String to;

        public String from;
        public String createName;
        public String createDate;

        public List<SplitStandItem> details;
    }

    private List<SplitTabel> step2(ExcelTable excelTable) {

        /***
         * 先定位到，每个采购单
         * 然后处理,那个采购单的每个明细
         */

        List<SplitTabel> reuslt = new ArrayList<>();

        SplitTabel table = null;

        List<ExcelRows> rows = excelTable.rows;

        ExcelRows item_row = null;

        ExcelCell cell = null;

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {

            item_row = rows.get(rowIndex);

            cell = item_row.cells.get(0);

            if (cell.val.equals("设计代号")) {
                table = step2Sub(excelTable, rowIndex);
                reuslt.add(table);
            }
        }
        return reuslt;
    }

    /**
     * 根据业务的解析表格
     *
     * @param excelTable
     * @return 当且业务的结构
     */
    private SplitTabel step2Sub(ExcelTable excelTable, int beginRowIndex) {

        List<ExcelRows> rows = excelTable.rows;

        // 解析头部
        SplitTabel splitTabel = step2_head(excelTable, beginRowIndex);

        boolean processOver = false;

        ExcelRows item_row = null;
        SplitStandItem item;
        int secondRowIndex = 0;
        for (int rowIndex = beginRowIndex; rowIndex < rows.size(); rowIndex++) {
            item_row = rows.get(rowIndex);
            ExcelCell cell = item_row.cells.get(0);
            if (cell.val.equals("设计代号")) {
                for (secondRowIndex = rowIndex + 1; secondRowIndex < rows.size(); secondRowIndex++) {
                    ExcelCell secondDell = rows.get(secondRowIndex).cells.get(0);
                    if (secondDell.val.isEmpty()) {
                        break;
                    }
                    item = null;
                    item = new SplitStandItem();
                    item.index = String.valueOf(secondDell.row);
                    item.code = getSpecialValue(rows, secondRowIndex, secondDell.col);
                    item.name = getSpecialValue(rows, secondRowIndex, secondDell.col + 1);
                    item.thick = getSpecialValue(rows, secondRowIndex, secondDell.col + 2);
                    item.width = getSpecialValue(rows, secondRowIndex, secondDell.col + 3);
                    item.length = getSpecialValue(rows, secondRowIndex, secondDell.col + 4);
                    item.material = getSpecialValue(rows, secondRowIndex, secondDell.col + 5);
                    item.number = getSpecialValue(rows, secondRowIndex, secondDell.col + 6);
                    item.remark = getSpecialValue(rows, secondRowIndex, secondDell.col + 7);
                    if (splitTabel.details == null) {
                        splitTabel.details = new ArrayList<>();
                    }
                    splitTabel.details.add(item);
                    processOver = true;
                }
                if (!processOver) {
                    throw new RuntimeException("采购单的明细,区域解析失败!");
                }
                break;
            }
        }

        step2_tail(splitTabel, excelTable, secondRowIndex);

        return splitTabel;
    }

    private SplitTabel step2_head(ExcelTable excelTable, int beginRowIndex) {

        SplitTabel splitTabel = new SplitTabel();

        List<ExcelRows> rows = excelTable.rows;

        ExcelRows item_row = null;

        ExcelCell cell = null;

        for (int rowIndex = beginRowIndex; rowIndex < rows.size(); rowIndex++) {

            item_row = rows.get(rowIndex);
            cell = item_row.cells.get(0);

            // 找到设计代号的时候,就不解析了
            if (cell.val.equals("设计代号")) {
                break;
            }

            // 解析完成了,退出
            if (cell.val.equals("TO:") || cell.val.indexOf("TO") != -1) {
                splitTabel.to = getSpecialValue(rows, rowIndex, cell.col + 1);
                splitTabel.from = getSpecialValue(rows, rowIndex, cell.col + 5);
                break;
            }

        }
        return splitTabel;
    }

    private void step2_tail(SplitTabel SplitTabel, ExcelTable excelTable, int beginRowIndex) {
        List<ExcelRows> rows = excelTable.rows;

        ExcelRows item_row = null;

        ExcelCell cell = null;

        for (int rowIndex = beginRowIndex; rowIndex < rows.size(); rowIndex++) {
            item_row = rows.get(rowIndex);
            cell = item_row.cells.get(0);

            // 找到采购单了，就不找了
            if (cell.val.equals("采购单")) {
                break;
            }
            // 解析完成了,退出
//            if (cell.val.indexOf("如不能满足纳期") != -1) {

//                ExcelRows createRow = rows.get(rowIndex + 2);
//                List<ExcelCell> tmep0 = createRow.cells.stream().filter(item -> item.val.indexOf("制表") != 1).collect(Collectors.toList());
//                if(tmep0 !=null && tmep0.size() ==1){
//                    SplitTabel.createName = getSpecialValue(rows, tmep0.get(0).row, tmep0.get(0).col+1);
//                }
//                ExcelRows createDateRow = rows.get(rowIndex + 3);
//                if (createDateRow.cells.get(5).val.indexOf("日期") != -1) {
//                    Date date = HSSFDateUtil.getJavaDate(Double.parseDouble(getSpecialValue(rows, rowIndex + 3, cell.col + 6)));
//                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                    SplitTabel.createDate = dateFormat.format(date);
//                }
//                break;
//            }

        }

    }

    private String getSpecialValue(List<ExcelRows> rows, int offsetIndex, int colIndex) {
        String result = null;

        ExcelRows row = rows.get(offsetIndex);

        List<ExcelCell> cols = row.cells;

        List<ExcelCell> cells = cols.stream().filter(item -> item.col == colIndex).collect(Collectors.toList());

        if (cells.isEmpty()) {
            result = "";
        } else if (cells == null || cells.size() == 0) {
            result = "";
        } else {
            ExcelCell cell = cells.get(0);
            result = cell.val;
        }

        return result;
    }

    @Resource
    BomPartPurchaseDetailService BomPartPurchaseDetailService;
    @Resource
    BomPartPurchaseService bomPartPurchaseService;

    private void step3(List<SplitTabel> splitTabels) {
        try {
            for (SplitTabel splitTabel : splitTabels) {
                if (splitTabel == null || splitTabel.details == null || splitTabel.details.size() == 0) {
                    continue;
                }

                if (true) {
                    savePurchaseDetail(splitTabel);
                    System.out.println("插入成功");
                }
            }
        } catch (Exception e) {
            System.out.println("插入失败" + e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    //    插入Purchase
    private Long savePurchase(SplitTabel splitTabel) {
        BomPartPurchase temp2 = new BomPartPurchase();
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = null;
//        try {
//            date = dateFormat.parse(splitTabel.createDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Timestamp timestamp = new Timestamp(date.getTime());
//
//        temp2.setCreater(splitTabel.createName);
//        temp2.setCreateTime(timestamp);

        temp2.setIssuer(splitTabel.to);
        temp2.setAccepter(splitTabel.from);
        if (bomPartPurchaseService.save(temp2)) {
            return temp2.getId();
        }
        return null;
    }

    //插入PurchaseDetail
    private void savePurchaseDetail(SplitTabel splitTabel) {
        List<BomPartPurchaseDetail> list = new ArrayList<>();

        BomPartPurchaseDetail temp = null;
        BomPartPurchaseDetail temp1 = null;

        for (SplitStandItem item : splitTabel.details) {
            temp = new BomPartPurchaseDetail();
            temp1 = new BomPartPurchaseDetail();
            temp.setCode(item.code);
            temp.setName(item.name);
            temp.setFirst(item.thick);
            temp.setSecond(item.width);
            temp.setThird(item.length);
            temp.setMaterial(item.material);
            temp1.setCode(item.code);
            temp1.setName(item.name);
            List<BomPartPurchaseDetail> detailList = BomPartPurchaseDetailService.getDetailList2(temp1);
            if (item.number.isEmpty()) {
                temp.setQuantity("0");
            } else {
                temp.setQuantity(item.number);
            }
            temp.setRemark(item.remark);
            if (detailList.size() ==0){
                list.add(temp);
            }else {
                Boolean update = BomPartPurchaseDetailService.update(temp);
                System.out.println(update);
            }

        }
        if (list.size()>0){
            boolean b = BomPartPurchaseDetailService.saveBatch(list);
            System.out.println(b);
        }



    }
}
