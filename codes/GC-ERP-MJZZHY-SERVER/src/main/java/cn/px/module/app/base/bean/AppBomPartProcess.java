package cn.px.module.app.base.bean;

import cn.px.module.general.bean.BaseBean;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author 品讯科技
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("bom_part_process")
public class AppBomPartProcess extends BaseBean {

    @ApiModelProperty(value = "订单主键id")
    private Long orderId;

    @ApiModelProperty(value = "订单产品对应的模具明细id")
    private Long orderDetailId;

    @ApiModelProperty(value = "模具对应的加工件id")
    private Long bomPartId;

    @ApiModelProperty(value = "工序id，对应数据字典中的字典id")
    private Long processId;


    @ApiModelProperty(value = "工序名，对应数据字典中的字典值")
    private String processName;

    @ApiModelProperty(value = "计划工时")
    private BigDecimal workHour;

    @ApiModelProperty(value = "计划完工日期")
    private Timestamp deadline;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "工序的编码")
    @TableField(exist = false)
    private String processCode;
}
