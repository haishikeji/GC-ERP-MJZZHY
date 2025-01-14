package cn.px.module.equipment.bean;

import cn.px.module.general.bean.BaseBean;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.util.List;

/**
 * @author 品讯科技
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Equipment extends BaseBean {

    @ApiModelProperty(value = "设备类别id")
    private Long categoryId;

    @ApiModelProperty(value = "设备生产国（通过数据字典管理）")
    private Long countryId;

    @ApiModelProperty(value = "设备生产厂商id")
    private Long equipmentManufacturerId;

    @ApiModelProperty(value = "设备生产厂商")
    private String equipmentManufacturer;

    @ApiModelProperty(value = "设备名称")
    private String name;

    @ApiModelProperty(value = "设备规格")
    private String standard;

    @ApiModelProperty(value = "设备数量")
    private String number;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    /** 虚字段 **/
    @ApiModelProperty(value = "设备生产国")
    @TableField(exist = false)
    private String country;

    @ApiModelProperty(value = "从表equipment_details")
    @TableField(exist = false)
    private List<EquipmentDetails> details;
}
