package cn.px.module.bom.bomPart.service;

import cn.px.module.baseMaterial.bean.BaseMaterial;
import cn.px.module.baseMaterial.dao.BaseMaterialDao;
import cn.px.module.bom.bomPart.bean.BomPart;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/***
 * 我是对一个模具订单的，导入。
 * 1：通过模具名称，模具编号，确定模具明细。
 * 2：加工件的材料没有找到，就跳过这条记录的插入。
 * 3：标准件，没有找到，也跳过这条记录
 * 2022-5-31 加工件，表批准件，分别的导入
 */
@Service
public class SplitExcelService {

    // 2022-6-21 追加，用于返回错误信息
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param fileUrl
     * @param sheetName
     */
    public void analysisExcelFile(String fileUrl, String sheetName, String mouldCode, String mouldName, String saleOrderId, String saleOrderProductId, String saleOrderDetailId, String type) {
        this.errorMessage = "";
        this.type = type;
        this.sale_order_id = Long.parseLong(saleOrderId);
        this.sale_order_product_id = Long.parseLong(saleOrderProductId);
        this.sale_order_detail_id = Long.parseLong(saleOrderDetailId);
        this.mouldCode = mouldCode;
        this.mouldName = mouldName;

        // 每次强行
        QueryWrapper<BaseMaterial> qbm = new QueryWrapper();
        this.baseMaterialList = baseMaterialDao.selectList(qbm);

        // 读取 excel文件，返回行列结构
        ExcelTable excelTable = this.step1(fileUrl, sheetName);

        // 解析内部数据结构
        SplitTabel splitTabel = this.step2(excelTable);

        // 转成表的bean，同时也是验证位置
        List<BomPart> bomPartList = this.step3(splitTabel);

        // 插入吧，少年
        this.step4(bomPartList);
    }

    // 标识是加工件，还是标准件
    private String type = "";
    private Long sale_order_id;
    private Long sale_order_product_id;
    private Long sale_order_detail_id;
    private String mouldCode;
    private String mouldName;

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
            //读取
            Sheet sheet = wb.getSheet(sheetName);//根据名称读取
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

    // 步骤2 区域
    private class SplitItem {
        // 序号
        public String index;

        // 编号
        public String code;

        // 名称
        public String label;


        // 材料
        public String materialCode;

        // 规格，长*宽*高
        public String size;

        // 数量
        public String num;

        // 下发时间
        public String nextTime;

        // 备注
        public String remark;
    }

    private class SplitProcessItem {
        public String index;
        public String name;
        public String length;
        public String width;
        public String height;
        public String materialCode;
        public String number;
        public String heart;
        public String remark;
    }

    private class SplitStandItem {
        // 修应该
        public String index;

        // 零件编号
        public String code;

        // 名称
        public String name;

        // 规格
        public String size;

        // 数量
        public String number;

        // 备注
        public String remark;
    }

    private class SplitTabel {
        // 做不区域
        // 模具名称
        public String modeName;
        // 管理号
        public String manageNo;
        //  日期
        public String date;
        // t0
        public String T0;


        // 模具类型
        public String modeType;
        // 设计
        public String design;
        // 编程
        public String program;
        // 钳工
        public String tongs;


        // 模具代号
        public String left1;
        // 投产代号
        public String left2;
        // 客户名称
        public String left3;
        // 提单时间
        public String left4;
        // 物料类别
        public String left5;

        // 设计者
        public String right1;
        // 纳期
        public String right2;
        // 采购者
        public String right3;
        // 采购时间
        public String right4;
        // 生产类别
        public String right5;

        // 加工件
        public List<SplitProcessItem> processList;

        // 标准件
        public List<SplitStandItem> standList;
    }

    /**
     * 根据业务的解析表格
     *
     * @param excelTable
     * @return 当且业务的结构
     */
    private SplitTabel step2(ExcelTable excelTable) {
        SplitTabel splitTabel = new SplitTabel();

        int begin_row = 0;
        int next_row = 0;
        next_row = this.step2_head(splitTabel, excelTable, begin_row);
        begin_row = next_row;
        if ("1".equals(this.type)) {
            next_row = this.step2_stand(splitTabel, excelTable, begin_row);
        } else if ("2".equals(this.type)) {
            next_row = this.step2_process(splitTabel, excelTable, begin_row);
        } else {
            throw new RuntimeException("没有传递要导入的类型,是标准件还是加工件?");
        }
        begin_row = next_row;
        return splitTabel;
    }

    // 头部
    private int step2_head(SplitTabel splitTabel, ExcelTable excelTable, int begin_row_index) {
        int next_row = 0;

        List<ExcelRows> rows = excelTable.rows;
        ExcelRows item_row = null;
        for (int rowIndex = begin_row_index; rowIndex < rows.size(); rowIndex++) {
            item_row = rows.get(rowIndex);

            // 标间件，加工件，都用这个一个头吧
            List<ExcelCell> cells = item_row.cells.stream().filter(item -> item.val.contains("标准件单") || item.val.contains("加工件明细表")).collect(Collectors.toList());
            if (cells.isEmpty()) {
                continue;
            }
            ExcelCell cell = cells.get(0);
            // 同行 不同列
            splitTabel.left1 = getSpecialValue(rows, rowIndex + 1, cell.col + 2);
            splitTabel.right1 = getSpecialValue(rows, rowIndex + 1, cell.col + 4);
            // 下一行
            splitTabel.left2 = getSpecialValue(rows, rowIndex + 2, cell.col + 2);
            splitTabel.right2 = getSpecialValue(rows, rowIndex + 2, cell.col + 4);
            // 下一行
            splitTabel.left3 = getSpecialValue(rows, rowIndex + 3, cell.col + 2);
            splitTabel.right3 = getSpecialValue(rows, rowIndex + 3, cell.col + 4);
            // 下一行
            splitTabel.left4 = getSpecialValue(rows, rowIndex + 4, cell.col + 2);
            splitTabel.right4 = getSpecialValue(rows, rowIndex + 4, cell.col + 4);
            // 下一行
            splitTabel.left5 = getSpecialValue(rows, rowIndex + 5, cell.col + 2);
            splitTabel.right5 = getSpecialValue(rows, rowIndex + 5, cell.col + 4);

            next_row = rowIndex + 5 + 1;
            break;
        }
        return next_row;
    }

    // 加工件
    private int step2_process(SplitTabel splitTabel, ExcelTable excelTable, int begin_row_index) {
        int next_row = 0;
        List<ExcelRows> rows = excelTable.rows;
        ExcelRows item_row = null;
        boolean over = false;
        for (int rowIndex = begin_row_index; rowIndex < rows.size(); rowIndex++) {
            item_row = rows.get(rowIndex);
            // 根据管理号+ 确定
            List<ExcelCell> cells = item_row.cells.stream().filter(item -> item.val.equals("序号")).collect(Collectors.toList());
            if (cells.isEmpty()) {
                continue;
            }
            ExcelCell cell = cells.get(0);

            int maxRecondes = 500;
            // 遍历序号下面的
            for (int tempIndex = rowIndex + 1; tempIndex < rows.size(); tempIndex++) {
                // 标准件,当序号为空的时候，就结束了
                String firstCol = getSpecialValue(rows, tempIndex, cell.col);

                // 没有序号就结束了
                if (firstCol.isEmpty()) {
                    next_row = tempIndex + 1;
                    over = true;
                    break;
                }
                if (splitTabel.processList == null) {
                    splitTabel.processList = new ArrayList<>();
                }
                SplitProcessItem splitItem = new SplitProcessItem();
                // 序号
                splitItem.index = getSpecialValue(rows, tempIndex, cell.col);
                splitItem.name = getSpecialValue(rows, tempIndex, cell.col + 1);
                splitItem.length = getSpecialValue(rows, tempIndex, cell.col + 2);
                splitItem.width = getSpecialValue(rows, tempIndex, cell.col + 4);
                splitItem.height = getSpecialValue(rows, tempIndex, cell.col + 6);
                splitItem.materialCode = getSpecialValue(rows, tempIndex, cell.col + 7);
                splitItem.number = getSpecialValue(rows, tempIndex, cell.col + 8);
                splitItem.heart = getSpecialValue(rows, tempIndex, cell.col + 9);
                splitItem.remark = getSpecialValue(rows, tempIndex, cell.col + 10);
                splitTabel.processList.add(splitItem);
                if (splitTabel.processList.size() > maxRecondes) {
                    throw new RuntimeException("导入的加工件记录数:超过了" + maxRecondes + "条");
                }
            }
            // 结束了
            if (over) {
                break;
            }
        }
        return next_row;
    }

    // 标准件
    private int step2_stand(SplitTabel splitTabel, ExcelTable excelTable, int begin_row_index) {
        int next_row = 0;
        List<ExcelRows> rows = excelTable.rows;
        ExcelRows item_row = null;
        boolean over = false;
        for (int rowIndex = begin_row_index; rowIndex < rows.size(); rowIndex++) {
            item_row = rows.get(rowIndex);
            // 标准件开始
            List<ExcelCell> cells = item_row.cells.stream().filter(item -> item.val.equals("序号")).collect(Collectors.toList());
            if (cells.isEmpty()) {
                continue;
            }
            ExcelCell cell = cells.get(0);
            // 不能超过500个件吧？
            int maxRecondes = 500;
            // 遍历标准价下2行
            for (int tempIndex = rowIndex + 1; tempIndex < rows.size(); tempIndex++) {
                // 当时标准件的时候，结束
                String firstCol = getSpecialValue(rows, tempIndex, cell.col);
                if (firstCol.isEmpty()) {
                    next_row = tempIndex + 1;
                    over = true;
                    break;
                }
                if (splitTabel.standList == null) {
                    splitTabel.standList = new ArrayList<>();
                }
                SplitStandItem splitItem = new SplitStandItem();
                // 序号
                splitItem.index = getSpecialValue(rows, tempIndex, cell.col);
                splitItem.code = getSpecialValue(rows, tempIndex, cell.col + 1);
                splitItem.name = getSpecialValue(rows, tempIndex, cell.col + 2);
                splitItem.size = getSpecialValue(rows, tempIndex, cell.col + 3);
                splitItem.number = getSpecialValue(rows, tempIndex, cell.col + 4);
                splitItem.remark = getSpecialValue(rows, tempIndex, cell.col + 5);
                splitTabel.standList.add(splitItem);
                if (splitTabel.standList.size() > maxRecondes) {
                    throw new RuntimeException("导入的标准件行数:超过了" + maxRecondes + "条!");
                }
            }
            // 完成了
            if (over) {
                break;
            }

        }
        return next_row;
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

    // 区域三
    // 用于材料的匹配,物料里面的材料
    @Resource
    private BaseMaterialDao baseMaterialDao;

    // 一次查找就够了
    private List<BaseMaterial> baseMaterialList;

    private List<BomPart> step3(SplitTabel splitTabel) {
        List<BomPart> result = new ArrayList<>();

        if (!this.mouldCode.equals(splitTabel.left1)) {
            throw new RuntimeException(this.mouldCode + "与" + splitTabel.left1 + "不匹配!");
        }
//
//        if (!this.moduleCode.equals(splitTabel.manageNo)) {
//            throw new RuntimeException("管理号:" + splitTabel.manageNo + ",在系统中没有找到!");
//        }
        if ("1".equals(this.type)) {
            List<BomPart> stand_list = null;
            stand_list = this.step3_stand(splitTabel);
            result.addAll(stand_list);
        } else if ("2".equals(this.type)) {
            List<BomPart> process_list = null;
            process_list = this.step3_process(splitTabel);
            result.addAll(process_list);
        }
        return result;
    }


    private int findTargetCount(String source, String target) {
        int count = 0;
        int index = 0;
        while ((index = source.indexOf(target, index)) != -1) {
            index = index + source.length();
            count++;
        }
        return count;
    }

    // 加工件，确保系统中有材料，然后导入其他数据
    private List<BomPart> step3_process(SplitTabel splitTabel) {
        List<BomPart> processList = new ArrayList<>();
        BomPart item = null;

        if (splitTabel.processList == null) {
            return processList;
        }
        List<BaseMaterial> currentFindMaterial = null;
        BaseMaterial findMaterial = null;

        // 加工件 厚宽长，有特殊的字符
        String findArr[] = new String[3];
        findArr[0] = "=";
        findArr[1] = "-";
        findArr[2] = "±";
        errorMessage = "";
        for (SplitProcessItem emp : splitTabel.processList) {
            // 没有零件名称就跳过吧
            if (emp.name.isEmpty()) {
                continue;
            }

            // 名称和便不能
            currentFindMaterial = this.baseMaterialList.stream().filter(temp -> temp.getName().equals(emp.materialCode)).collect(Collectors.toList());
            if (currentFindMaterial == null || currentFindMaterial.size() == 0) {
                //  throw new RuntimeException("加工件的材料：" + emp.materialCode + ",在系统中没有找到!");
                // 2022-4-13 神通模具的业务是，找不到就跳过，不插入
                errorMessage = errorMessage + "零件名称" + emp.name + "的材质" + emp.materialCode + ",在系统中没有找到。<br/>";
                continue;
            }
            if (currentFindMaterial.size() > 1) {
                throw new RuntimeException("加工件的材料：" + emp.materialCode + ",在系统中找到了多条记录!");
            }
            // 没有序号，还零件名称的时候就跳过
            if (emp.index.isEmpty() && emp.name.isEmpty()) {
                continue;
            }
            findMaterial = currentFindMaterial.get(0);
            item = new BomPart();
            // 因为要新的插入啊
            // item.setId();
            // item.setDesignAssignmentId(this.design_assignment_id);
            item.setSaleOrderId(this.sale_order_id);
            item.setSaleOrderProductId(this.sale_order_product_id);
            item.setSaleOrderDetailId(this.sale_order_detail_id);
            item.setType("加工件");

            // 2022-6-10 行号,要成为序号
            if (!emp.index.isEmpty()) {
                // item.setSort(Float.valueOf(emp.index).intValue() + "");
                // 2022-7-5
                // 3-1 等这样的序号
                item.setSort(emp.index);
            } else {
                item.setSort("1");
            }

            // 老版本的表，弃用了
            item.setBaseMaterialId(findMaterial.getId());
            item.setBaseMaterialCode(findMaterial.getCode());
            item.setBaseMaterialName(findMaterial.getName());

            // 名称
            item.setPartName(emp.name);

            // 长 宽 高
//            if (!emp.length.isEmpty()) {
//                item.setLength(new BigDecimal(emp.length));
//            } else {
//                item.setLength(new BigDecimal(0));
//            }
            if (!emp.length.isEmpty()) {
                Boolean find = false;
                for (int i = 0; i < findArr.length; i++) {
                    String temp = findArr[i];
                    if (emp.length.contains(temp)) {
                        String temp1 = emp.length.substring(0, emp.length.indexOf(temp));
                        String temp2 = emp.length.substring(emp.length.indexOf(temp), emp.length.length());
                        try {
                            item.setLength(new BigDecimal(temp1));
                            item.setAddLength(temp2);
                        } catch (Exception ex) {
                            item.setLength(new BigDecimal(0));
                            item.setAddLength("0");
                        }
                        find = true;
                    }
                }
                // 没有找到特殊字符
                if (!find) {
                    try {
                        item.setLength(new BigDecimal(emp.length));
                        item.setAddLength("0");
                    } catch (Exception ex) {
                        item.setLength(new BigDecimal(0));
                        item.setAddLength("0");
                    }
                }
//                if (emp.length.contains("±")) {
//                    String temp1 = emp.length.substring(0, emp.length.indexOf("±"));
//                    String temp2 = emp.length.substring(emp.length.indexOf("±"), emp.length.length());
//                    if (temp1.isEmpty()) {
//                        item.setLength(new BigDecimal(0));
//                        item.setAddLength(new BigDecimal(0));
//                    } else {
//                        item.setLength(new BigDecimal(temp1));
//                        item.setAddLength(new BigDecimal(temp2));
//                    }
//                } else {
//                    item.setLength(new BigDecimal(emp.length));
//                    item.setAddLength(new BigDecimal(0));
//                }
            } else {
                item.setLength(new BigDecimal(0));
                item.setAddLength("0");
            }


//            if (!emp.width.isEmpty()) {
//                item.setWidth(new BigDecimal(emp.width));
//            } else {
//                item.setWidth(new BigDecimal(0));
//            }
            // 考虑到 加量
            // "±"
            if (!emp.width.isEmpty()) {
                Boolean find = false;
                for (int i = 0; i < findArr.length; i++) {
                    String temp = findArr[i];
                    if (emp.width.contains(temp)) {
                        String temp1 = emp.width.substring(0, emp.width.indexOf(temp));
                        String temp2 = emp.width.substring(emp.width.indexOf(temp), emp.width.length());
                        try {
                            item.setWidth(new BigDecimal(temp1));
                            item.setAddWidth(temp2);
                        } catch (Exception ex) {
                            item.setWidth(new BigDecimal(0));
                            item.setAddWidth("0");
                        }
                        find = true;
                    }
                }
                if (!find) {
                    try {
                        item.setWidth(new BigDecimal(emp.width));
                        item.setAddWidth("0");
                    } catch (Exception ex) {
                        item.setWidth(new BigDecimal(0));
                        item.setAddWidth("0");
                    }
                }

//                if (emp.width.contains("±")) {
//                    String temp1 = emp.width.substring(0, emp.width.indexOf("±"));
//                    String temp2 = emp.width.substring(emp.width.indexOf("±"), emp.width.length());
//                    if (temp1.isEmpty()) {
//                        item.setWidth(new BigDecimal(0));
//                        item.setAddWidth(new BigDecimal(0));
//                    } else {
//                        item.setWidth(new BigDecimal(temp1));
//                        item.setAddWidth(new BigDecimal(temp2));
//                    }
//                } else {
//                    item.setWidth(new BigDecimal(emp.width));
//                    item.setAddWidth(new BigDecimal(0));
//                }
            } else {
                item.setWidth(new BigDecimal(0));
                item.setAddWidth("0");
            }

            // "±"
            if (!emp.height.isEmpty()) {

                Boolean find = false;
                for (int i = 0; i < findArr.length; i++) {
                    String temp = findArr[i];
                    if (emp.height.contains(temp)) {
                        String temp1 = emp.height.substring(0, emp.height.indexOf(temp));
                        String temp2 = emp.height.substring(emp.height.indexOf(temp), emp.height.length());
                        try {
                            item.setThickness(new BigDecimal(temp1));
                            item.setAddThick(temp2);
                        } catch (Exception ex) {
                            item.setThickness(new BigDecimal(0));
                            item.setAddThick("0");
                        }
                        find = true;
                    }
                }
                if (!find) {
                    try {
                        item.setThickness(new BigDecimal(emp.height));
                        item.setAddThick("0");
                    } catch (Exception ex) {
                        item.setThickness(new BigDecimal(0));
                        item.setAddThick("0");
                    }
                }


//                if (emp.height.contains("±")) {
//                    String temp1 = emp.height.substring(0, emp.height.indexOf("±"));
//                    String temp2 = emp.height.substring(emp.height.indexOf("±") + "±".length(), emp.height.length());
//                    if (temp1.isEmpty()) {
//                        item.setThickness(new BigDecimal(0));
//                        item.setAddThick(new BigDecimal(0));
//                    } else {
//                        item.setThickness(new BigDecimal(temp1));
//                        item.setAddThick(new BigDecimal(temp2));
//                    }
//                } else {
//                    item.setThickness(new BigDecimal(emp.height));
//                    item.setAddThick(new BigDecimal(0));
//                }
            } else {
                item.setThickness(new BigDecimal(0));
                item.setAddThick("0");
            }
            // 热处理
            if (!emp.heart.isEmpty()) {
                item.setHeatTreatment("是");
            } else {
                item.setHeatTreatment("否");
            }

            // 数量
            if (!emp.number.isEmpty()) {
                item.setNumber(Float.valueOf(emp.number).intValue());
            } else {
                item.setNumber(1);
            }
            item.setRemark(emp.remark);
            processList.add(item);
        }
        return processList;
    }

    // 标准件 确保中系统存在，名称和尺寸尊在
    private List<BomPart> step3_stand(SplitTabel splitTabel) {
        List<BomPart> standList = new ArrayList<>();
        BomPart item = null;

        if (splitTabel.standList == null) {
            return standList;
        }
        errorMessage = "";
        for (SplitStandItem emp : splitTabel.standList) {
            if (emp.code.isEmpty() && emp.name.isEmpty()) {
                continue;
            }
            emp.name = emp.name.replace(".0","");
            emp.code = emp.code.replace(".0","");
            emp.size = emp.size.replace(".0","");

            // 标准件得名称和编码不存在
            QueryWrapper<BaseMaterial> queryBaseMaterial = new QueryWrapper<>();
            if (!emp.name.isEmpty()) {
                queryBaseMaterial.eq("name", emp.name);
            }
            if (!emp.code.isEmpty()) {
                queryBaseMaterial.eq("code", emp.code);
            }
            queryBaseMaterial.eq("standard", emp.size);
            BaseMaterial result = baseMaterialDao.selectOne(queryBaseMaterial);

            item = new BomPart();
            // 2022-4-12。找到用数据库，没找到，用文件中的
            if (result == null) {

                // 2022-4-13 神通模具的业务是，找不到，就不插入这条记录。
                errorMessage = errorMessage + (!emp.name.isEmpty() ? emp.name : emp.code) + emp.size + "在系统中没有找到。<br/>";
                continue;

                // 2022-4-12 如果标准件，不存在的化，就不抛出异常了
                // throw new RuntimeException("标准件:" + emp.label + ","+emp.code+","+emp.size+"在系统中没有找到");
                // 2022-4-12 标准件，可以没有
//                item.setBaseMaterialId(0L);
//                item.setBaseMaterialName(emp.label);
//                item.setBaseMaterialCode(emp.code);
            } else {
                // 标准件，就用这万一
                item.setBaseMaterialId(result.getId());
                item.setBaseMaterialName(result.getName());
                item.setBaseMaterialCode(result.getCode());
            }


            // 因为要新的插入啊
            // item.setId();
            // item.setDesignAssignmentId(this.design_assignment_id);
            item.setSaleOrderId(this.sale_order_id);
            item.setSaleOrderProductId(this.sale_order_product_id);
            item.setSaleOrderDetailId(this.sale_order_detail_id);
            item.setType("标准件");

            // 2022-6-10 行号,要成为序号
            if (!emp.index.isEmpty()) {
                //  item.setSort(Float.valueOf(emp.index).intValue() + "");
                item.setSort(emp.index);
            } else {
                item.setSort("1");
            }

            // 名称
            item.setPartName(emp.name);
            // 图纸编号
            item.setPartCode(emp.code);

            // 这个是标准件得规格
            item.setStandard(emp.size);

            // 设必填项啊
            item.setThickness(new BigDecimal(0));
            item.setLength(new BigDecimal(0));
            item.setWidth(new BigDecimal(0));

            // 标准件没有
            // item.setMaterialId(0L);

            // 数量
            if (!emp.number.isEmpty()) {
                item.setNumber(Float.valueOf(emp.number).intValue());
            } else {
                item.setNumber(1);
            }
            item.setRemark(emp.remark);
            standList.add(item);
        }
        return standList;
    }

    // 步骤4
    @Resource
    private BomPartService bomPartService;

    // 插入吧
    private void step4(List<BomPart> bomPartList) {
        if (bomPartList.size() >= 1) {
            //  bomPartService.saveBatch(bomPartList);
            replaceInsert(bomPartList);
        }
    }

    /***
     * 2023-04-27
     * 如果插入的已经存在，那么久跳过插入
     * @param bomPartList
     */
    private void replaceInsert(List<BomPart> bomPartList) {

        List<BomPart> insert = new ArrayList<>();


        List<BomPart> find = null;

        for (BomPart item : bomPartList) {

            QueryWrapper<BomPart> queryWrapper = new QueryWrapper<>();

            if (item.getType().equals("标准件")) {

                queryWrapper.eq("sale_order_detail_id",item.getSaleOrderDetailId());
                queryWrapper.eq("type", "标准件");
                queryWrapper.eq("base_material_name", item.getBaseMaterialName());
                queryWrapper.eq("part_name", item.getPartName());
                queryWrapper.eq("standard", item.getStandard());
                queryWrapper.eq("number", item.getNumber());
                queryWrapper.eq("deleted", 0);

            } else {

                queryWrapper.eq("sale_order_detail_id",item.getSaleOrderDetailId());
                queryWrapper.eq("type", "加工件");
                queryWrapper.eq("part_name", item.getPartName());
                queryWrapper.eq("base_material_name", item.getBaseMaterialName());
                queryWrapper.eq("length", item.getLength());
                queryWrapper.eq("width", item.getWidth());
                queryWrapper.eq("thickness", item.getThickness());
                queryWrapper.eq("heat_treatment", item.getHeatTreatment());
                queryWrapper.eq("number", item.getNumber());
            }

            find = bomPartService.list(queryWrapper);

            if (find != null && find.size() >= 1) {

//                if (item.getType().equals("标准件")) {
//                    errorMessage = errorMessage + "标准件:" + item.getPartName() + " 已存在,跳过导入。<br/>";
//                } else {
//                    errorMessage = errorMessage + "加工件:" + item.getPartName() + " 已存在,跳过导入。<br/>";
//                }
                continue;
            }

            insert.add(item);
        }

        // 插入
        if (insert != null && insert.size() >= 1) {
            bomPartService.saveBatch(insert);
        }

    }
}
