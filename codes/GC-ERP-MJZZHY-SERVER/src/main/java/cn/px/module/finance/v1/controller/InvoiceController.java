package cn.px.module.finance.v1.controller;

import cn.px.module.general.bean.Result;
import cn.px.module.company.bean.Company;
import cn.px.module.company.service.CompanyService;
import cn.px.module.contract.bean.Contract;
import cn.px.module.contract.service.ContractService;

import cn.px.module.finance.v1.bean.Invoice;
import cn.px.module.finance.v1.bean.InvoiceDetail;
import cn.px.module.finance.v1.dao.InvoiceDao;
import cn.px.module.finance.v1.model.ChoiceContractDialogModel;
import cn.px.module.finance.v1.model.ChoiceInvoiceDialogModel;
import cn.px.module.finance.v1.service.InvoiceDetailService;
import cn.px.module.finance.v1.service.InvoiceService;
import cn.px.utils.Rutils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "Equipment数据接口")
@RequestMapping(value = "/module/invoice")
public class InvoiceController {

    @Resource
    InvoiceService invoiceService;
    @Resource
    InvoiceDetailService invoiceDetailService;
    @Resource
    InvoiceDao invoiceDao;

    @Resource
    CompanyService companyService;
    @Resource
    ContractService contractService;

    @ApiOperation(value = "查询Equipment", notes = "根据id查询Equipment")
    @GetMapping("/getById/{id}")
    @ResponseBody
    public Result getById(@PathVariable Long id) {
        try {

            Invoice invoice = invoiceService.getById(id);
            if (invoice != null) {
                Company company = companyService.getById(invoice.getCustomerId());
                invoice.setCompanyName(company != null ? company.getName() : "");

                Contract contract = contractService.getById(invoice.getContractId());
                invoice.setContractNo(contract.getFormNo());

                // 客户的预付款
                QueryWrapper<Invoice> qyIn = new QueryWrapper<>();
                qyIn.eq("customer_id", invoice.getCustomerId());
                qyIn.eq("deleted", 0);
                List<Invoice> list = invoiceService.list(qyIn);
                BigDecimal amountBalance = list.stream().map(Invoice::getInOutAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                invoice.setAmountBalance(amountBalance);
                invoice.setAmountBalance0(amountBalance);
            }

            Map<String, Object> conditions = new HashMap<>();
            conditions.put("invoiceId", id);
            List<InvoiceDetail> invoiceDetails = invoiceDetailService.getList(conditions);
            invoice.setDetails(invoiceDetails);

            Map<String, Object> map = new HashMap<>(16);
            map.put("formData", invoice);
            return Rutils.success(map);
        } catch (Exception e) {
            return Rutils.serverError();
        }
    }

    @ApiOperation(value = "带条件List 分页查询", notes = "带条件List 分页查询")
    @PostMapping("/getList")
    @ResponseBody
    public Result getList(@RequestBody Map<String, Object> conditions) {
        Map<String, Object> map = new HashMap<>(16);
        Integer pn = conditions.get("pn") == null ? null : Integer.parseInt(conditions.get("pn").toString());
        Integer size = conditions.get("size") == null ? null : Integer.parseInt(conditions.get("size").toString());
        Integer start = (pn == null || size == null) ? null : (pn - 1) * size;
        conditions.put("start", start);
        //分页数据
        map.put("list", invoiceService.getList(conditions));
        if (start != null) {
            //总页数
            map.put("total", invoiceService.getTotal(conditions));
        }
        return Rutils.success(map);
    }

    @ApiOperation(value = "删除Equipment", notes = "根据id删除Equipment")
    @DeleteMapping("/deleteById/{id}")
    @ResponseBody
    public Result deleteById(@PathVariable Long id) {
        if (invoiceService.removeById(id)) {
            return Rutils.success();
        } else {
            return Rutils.error();
        }
    }

    // 不是最后一条记录不能删除，修改
    private void Check(Invoice arg) {
        //  List<Invoice> list = invoiceService.getList();
    }

    @ApiOperation(value = "批量删除Equipment", notes = "批量删除Equipment")
    @PostMapping("/batchDelete")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Result batchDelete(@RequestBody List<Invoice> list) {

        Result result = null;

        try {
            if (list.size() != 1 ) {
                throw new RuntimeException("禁止批量删除");
            }
            QueryWrapper<InvoiceDetail> qwId = new QueryWrapper<>();
            qwId.eq("invoice_id",list.get(0).getId());
            List<InvoiceDetail> detail = invoiceDetailService.list(qwId);
            invoiceDetailService.removeBatchByIds(detail);

            if (invoiceService.removeBatchByIds(list)) {
                return Rutils.success();
            } else {
                return Rutils.error();
            }
        } catch (RuntimeException ex) {
            result = Rutils.error(ex.getMessage());
        } catch (Exception ex) {
            result = Rutils.serverError(ex.getMessage());
        }
        return result;
    }

    @ApiOperation(value = "整体保存", notes = "整体保存")
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public Result save(@RequestBody Invoice invoice) {
        try {
            boolean bool = invoiceService.saveOrUpdate(invoice);
            if (bool) {
                List<InvoiceDetail> detailList = invoice.getDetails();
                if (!detailList.isEmpty()) {
                    Map<String, Object> conditions = new HashMap<>(16);
                    conditions.put("invoiceId", invoice.getId());
                    invoiceDetailService.physicallyDelete(conditions);
                    for (InvoiceDetail invoiceDetail : detailList) {
                        invoiceDetail.setInvoiceId(invoice.getId());
                    }
                    bool = invoiceDetailService.saveBatch(detailList);
                }
            }
            if (bool) {
                return Rutils.success(invoice);
            } else {
                return Rutils.error();
            }
        } catch (Exception e) {
            return Rutils.serverError();
        }
    }

    @ApiOperation(value = "表单上面返回的合同选择", notes = "表单上面返回的合同选择")
    @PostMapping("/ChoiceContractDialog")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public Result ChoiceContractDialog(@RequestBody Map<String, Object> conditions) {
        Result result = null;

        Integer pn = conditions.get("pn") == null ? null : Integer.parseInt(conditions.get("pn").toString());
        Integer size = conditions.get("pageSize") == null ? null : Integer.parseInt(conditions.get("pageSize").toString());
        Integer start = (pn == null || size == null) ? null : (pn - 1) * size;
        conditions.put("start", start);

        List<ChoiceContractDialogModel> list = invoiceDao.ChoiceContractDialog(conditions);
        int total = invoiceDao.ChoiceContractDialogTotal(conditions);
        Map<String, Object> map = new HashMap<>(16);
        map.put("list", list);
        map.put("total", total);
        result = Rutils.success(map);
        return result;
    }

    @ApiOperation(value = "表单上面返回的合同选择", notes = "表单上面返回的合同选择")
    @PostMapping("/ChoiceInvoiceDialog")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public Result ChoiceInvoiceDialog(@RequestBody Map<String, Object> conditions) {
        Result result = null;
        try {
            String contractId = conditions.get("contractId") != null ? conditions.get("contractId").toString() : "";
            if (contractId.isEmpty()) {
                throw new RuntimeException("没有提供合同id!");
            }
            Integer pn = conditions.get("pn") == null ? null : Integer.parseInt(conditions.get("pn").toString());
            Integer size = conditions.get("pageSize") == null ? null : Integer.parseInt(conditions.get("pageSize").toString());
            Integer start = (pn == null || size == null) ? null : (pn - 1) * size;
            conditions.put("start", start);
            List<ChoiceInvoiceDialogModel> list = invoiceDao.ChoiceInvoiceDialog(conditions);
            int total = invoiceDao.ChoiceInvoiceDialogTotal(conditions);
            Map<String, Object> map = new HashMap<>(16);
            map.put("list", list);
            map.put("total", total);
            result = Rutils.success(map);
        } catch (RuntimeException ex) {
            result = Rutils.error(ex.getMessage());
        } catch (Exception ex) {
            result = Rutils.error(ex.getMessage());
        }
        return result;
    }
}
