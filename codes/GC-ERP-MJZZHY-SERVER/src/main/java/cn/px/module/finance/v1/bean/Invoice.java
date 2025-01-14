package cn.px.module.finance.v1.bean;

import cn.px.module.general.bean.BaseBean;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Invoice extends BaseBean {
    private Long customerId;
    private Long contractId;
    private String voucherNo;
    private BigDecimal amount;
    private String remark;
    // 增加，或使用，预付款
    private BigDecimal inOutAmount;


    @ApiModelProperty(value = "从表equipment_details")
    @TableField(exist = false)
    private List<InvoiceDetail> details;

    @TableField(exist = false)
    private String companyName;
    @TableField(exist = false)
    private String contractNo;

    @ApiModelProperty(value = "我是预付款")
    @TableField(exist = false)
    private BigDecimal amountBalance;
    @ApiModelProperty(value = "我是预付款")
    @TableField(exist = false)
    private BigDecimal amountBalance0;


    @ApiModelProperty(value = "不是最后一条，不能修改，编辑删除")
    @TableField(exist = false)
    private String notEdit;
}
