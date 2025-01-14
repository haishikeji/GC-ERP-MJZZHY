package cn.px.module.finance.v2.controller;


import cn.px.module.general.bean.Result;
import cn.px.module.finance.v2.bean.PaymentSingleReceivableInvoiceDetail;

import cn.px.module.finance.v2.dao.SingleInvoiceReceivableDao;

import cn.px.module.finance.v2.model.*;

import cn.px.module.finance.v2.service.PaymentSingleReceivableInvoiceDetailService;
import cn.px.utils.Rutils;
import cn.px.utils.jklss.JxlsBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2022-12-5 新版本的发票与回款业务
 * 将发票与回款放在一个页面
 * dao，以及控制器的函数名，根据页面布局来的。
 * 注释，我是单件的
 */

@RestController
@Api(tags = "Equipment数据接口")
@RequestMapping(value = "/module/singleInvoiceReceivable")
public class SingleInvoiceReceivableController {

    @Resource
    SingleInvoiceReceivableDao singleInvoiceReceivableDao;

    @ApiOperation(value = "带条件List 分页查询", notes = "带条件List 分页查询")
    @PostMapping("/getListLeft")
    @ResponseBody
    public Result getListLeft(@RequestBody Map<String, Object> conditions) {
        Map<String, Object> map = new HashMap<>(16);
        Integer pn = conditions.get("pn") == null ? null : Integer.parseInt(conditions.get("pn").toString());
        Integer size = conditions.get("size") == null ? null : Integer.parseInt(conditions.get("size").toString());
        Integer start = (pn == null || size == null) ? null : (pn - 1) * size;
        conditions.put("start", start);
        //分页数据
        map.put("list", singleInvoiceReceivableDao.getListLeft(conditions));
        if (start != null) {
            //总页数
            // map.put("total", invoiceService.getTotal(conditions));
        }
        return Rutils.success(map);
    }

    @ApiOperation(value = "带条件List 分页查询", notes = "带条件List 分页查询")
    @PostMapping("/getListDown")
    @ResponseBody
    public Result getListDown(@RequestBody Map<String, Object> conditions) {
        Map<String, Object> map = new HashMap<>(16);
        Integer pn = conditions.get("pn") == null ? null : Integer.parseInt(conditions.get("pn").toString());
        Integer size = conditions.get("size") == null ? null : Integer.parseInt(conditions.get("size").toString());
        Integer start = (pn == null || size == null) ? null : (pn - 1) * size;
        conditions.put("start", start);
        //分页数据
        List<SingleDownModel> lst = singleInvoiceReceivableDao.getListDown(conditions);
        map.put("list", lst);
        if (start != null) {
            //总页数
            // map.put("total", invoiceService.getTotal(conditions));
        }
        return Rutils.success(map);
    }

    @Resource
    PaymentSingleReceivableInvoiceDetailService paymentSingleReceivableInvoiceDetailService;

    @ApiOperation(value = "保存发票明细", notes = "保存发票明细")
    @PostMapping("/detailSave")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public Result detailSave(@RequestBody PaymentSingleReceivableInvoiceDetail arg) {
        Result result = null;
        try {
            paymentSingleReceivableInvoiceDetailService.saveOrUpdate(arg);
            result = Rutils.success();
        } catch (RuntimeException ex) {
            result = Rutils.error(ex.getMessage());
        } catch (Exception ex) {
            result = Rutils.serverError(ex.getMessage());
        }
        return result;
    }


    @ApiOperation(value = "删除发票明细", notes = "删除发票明细")
    @PostMapping("/detailDel")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Result detailDel(@RequestBody List<PaymentSingleReceivableInvoiceDetail> list) {
        Result result = null;
        try {
            if (list.size() != 1 ) {
                throw new RuntimeException("禁止批量删除");
            }
            paymentSingleReceivableInvoiceDetailService.removeBatchByIds(list);

            return Rutils.success();
        } catch (RuntimeException ex) {
            result = Rutils.error(ex.getMessage());
        } catch (Exception ex) {
            result = Rutils.serverError(ex.getMessage());
        }
        return result;
    }

    @Value("${file.download.path}")
    private String filePath;

    @Value("${file.download.url}")
    private String downLoadPath;

    @ApiOperation(value = "带条件List 分页查询", notes = "带条件List 分页查询")
    @PostMapping("/exportDownList")
    @ResponseBody
    public Result exportDownList(@RequestBody Map<String, Object> conditions) throws Exception {
        Result result = null;
        try {

            String contractId = conditions.get("contractId") == null ? "" : conditions.get("contractId").toString();

            if (contractId.isEmpty()) {
                throw  new RuntimeException("没有传递合同id!");
            }

            List<SingleLeft1Model> heads = null;
            SingleLeft1Model head = null;
            List<SingleDownModel> details = null;
            ExportDownModel sum = null;


            heads = singleInvoiceReceivableDao.getListLeft(conditions);
            head = heads.get(0);
            details = singleInvoiceReceivableDao.getListDown(conditions);

            if (head != null) {
                if (head.getDeadline() != null) {
                    Date date = new Date(head.getDeadline().getTime());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String dateStr = sdf.format(date);
                    head.setDeadlineStr(dateStr);
                }
            }

            if (details != null && details.size() >= 1) {
                BigDecimal sumBorrowAmount = details.stream().map(SingleDownModel::getBorrowAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal sumStayAmount = details.stream().map(SingleDownModel::getStayAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal sumBalance = sumBorrowAmount.subtract(sumStayAmount);
                sum = new ExportDownModel();
                sum.setSumBorrowAmount(sumBorrowAmount);
                sum.setSumStayAmount(sumStayAmount);
                sum.setSumBalance(sumBalance);
            }


            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
            String exportName = "单件发票与回款";
            exportName = exportName + "_" + format.format(date).toString();
            String fileName = exportName + ".xlsx";
            String outPath = this.filePath + fileName; // "D:/out_process_card.xlsx";

            File fileCheck = new File(outPath);
            if (fileCheck.exists()) {
                fileCheck.delete();
            }
            JxlsBuilder jxlsBuilder = JxlsBuilder
                    .getBuilder("excels/singleContractInvoice.xlsx")
                    .out(outPath)
                    .putVar("head", head)
                    .putVar("details", details)
                    .putVar("sum", sum)
                    .ignoreImageMiss(true)
                    .build();
            System.out.println("导出成功");
            System.out.println(jxlsBuilder.getOutFile().getAbsolutePath());
            String exportPath = jxlsBuilder.getOutFile().getAbsolutePath();
            // return fileName;


            String downLoadAllPath = downLoadPath.replace("**", "") + fileName;
            Map<String, Object> map = new HashMap<>(16);
            map.put("fileName", downLoadAllPath);
            result = Rutils.success(map);

        }catch (RuntimeException ex){
            result = Rutils.error(ex.getMessage());
        }
        catch (Exception ex){
            result = Rutils.serverError(ex.getMessage());
        }
        return result;
    }
}
