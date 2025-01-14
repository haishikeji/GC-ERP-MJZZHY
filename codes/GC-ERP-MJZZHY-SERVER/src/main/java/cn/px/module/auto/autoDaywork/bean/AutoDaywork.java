package cn.px.module.auto.autoDaywork.bean;

import cn.px.module.general.bean.BaseBean;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

import io.swagger.annotations.ApiModelProperty;
import lombok.experimental.Accessors;
import lombok.EqualsAndHashCode;


/**
 * @author 品讯科技
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AutoDaywork extends BaseBean {

    @ApiModelProperty(value = "订单主键id")
    private Long autoOrderId;

    @ApiModelProperty(value = "订单产品对应的模具明细id")
    private Long autoOrderDetailId;

    @ApiModelProperty(value = "模具对应的加工件id")
    private Long autoBomPartId;

    @ApiModelProperty(value = "模具对应的加工件工序id")
    private Long autoBomPartProcessId;

    @ApiModelProperty(value = "实际工时")
    private BigDecimal actualWorkHour;

    @ApiModelProperty(value = "实际开始时间")
    private Timestamp actualStartTime;

    @ApiModelProperty(value = "实际结束时间")
    private Timestamp actualEndTime;

    @ApiModelProperty(value = "操作者id")
    private Long employeeId;

    @ApiModelProperty(value = "加工设备id")
    private Long equipmentDetailsId;

    @ApiModelProperty(value = "完成数量")
    private Integer number;

    @ApiModelProperty(value = "加工状态")
    private String status;

    @ApiModelProperty(value = "备注")
    private String remark;


    @ApiModelProperty(value = "当前的数据字典的编辑状态")
    @TableField(exist = false)
    private String employeeName;

    @ApiModelProperty(value = "当前的数据字典的编辑状态")
    @TableField(exist = false)
    private String employeeCode;

    @ApiModelProperty(value = "设备编号")
    @TableField(exist = false)
    private String equipmentCode;

    @ApiModelProperty(value = "当前的编辑状态")
    @TableField(exist = false)
    private Boolean editStatus = false;

    @TableField(exist = false)
    private String processName;

    @TableField(exist = false)
    private Long sort;
}
