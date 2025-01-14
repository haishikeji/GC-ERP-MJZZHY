package cn.px.module.auto.autoBom.bean;

import cn.px.module.auto.autoDaywork.bean.AutoDaywork;
import cn.px.module.process.base.bean.ProcessDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableField;
import cn.px.module.general.bean.BaseBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.experimental.Accessors;
import lombok.EqualsAndHashCode;

import java.util.List;


/**
 * @author 品讯科技
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AutoBomPartProcess extends BaseBean {

    @ApiModelProperty(value = "订单主键id")
    private Long autoOrderId;

    @ApiModelProperty(value = "订单产品对应的模具明细id")
    private Long autoOrderDetailId;

    @ApiModelProperty(value = "模具对应的加工件id")
    private Long autoBomPartId;

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

    /**
     * 虚拟字段，数据库中不存在
     */
    @ApiModelProperty(value = "工序编码")
    @TableField(exist = false)
    private String processCode;

    @ApiModelProperty(value = "前端拖动排序用的uuid")
    @TableField(exist = false)
    private String uuid;

    @ApiModelProperty(value = "当前的数据字典的编辑状态")
    @TableField(exist = false)
    private Boolean editStatus = false;

    @ApiModelProperty(value = "每道工序的全部报工")
    @TableField(exist = false)
    private List<AutoDaywork> dayworks;


    // 2022-5-10 每道工序的加工内容
    @TableField(exist = false)
    private List<ProcessDetail> processDetails;


    /***
     * 2023-03-09 不了下面2个字段用于 完成占比
     */
    @TableField(exist = false)
    private String dayWorkCnt;
    @TableField(exist = false)
    private String dayWorkComCnt;
}
