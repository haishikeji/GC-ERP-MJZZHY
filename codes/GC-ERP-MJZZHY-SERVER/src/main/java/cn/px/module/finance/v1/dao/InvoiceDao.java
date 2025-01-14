package cn.px.module.finance.v1.dao;

import cn.px.module.finance.v1.bean.Invoice;
import cn.px.module.finance.v1.model.ChoiceContractDialogModel;
import cn.px.module.finance.v1.model.ChoiceInvoiceDialogModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

public interface InvoiceDao  extends BaseMapper<Invoice> {

    /**
     * 带条件的查询
     * @param conditions 条件
     * @return List 集合
     */
    List<Invoice> getList(Map<String,Object> conditions);

    /**
     * 带条件的查询总数
     * @param conditions 条件
     * @return int 查询总数量
     */
    int getTotal(Map<String,Object> conditions);


    // 表单上的合同对话框
    List<ChoiceContractDialogModel> ChoiceContractDialog(Map<String,Object> conditions);
    int ChoiceContractDialogTotal(Map<String,Object> conditions);

    // 明细
    List<ChoiceInvoiceDialogModel> ChoiceInvoiceDialog(Map<String,Object> conditions);
    int ChoiceInvoiceDialogTotal(Map<String,Object> conditions);

}
