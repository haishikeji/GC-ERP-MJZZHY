package cn.px.module.mould.mouldStore.bean;

import cn.px.module.general.bean.BaseBean;
import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import lombok.experimental.Accessors;
import lombok.EqualsAndHashCode;

/**
 * @author 品讯科技
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class MouldFinishStoreOutDetail extends BaseBean {
    @ApiModelProperty(value = "")
    private String line;

    @ApiModelProperty(value = "")
    private Long mouldFinishStoreOutId;

    @ApiModelProperty(value = "")
    private Long mouldFinishStoreId;

    @ApiModelProperty(value = "")
    private String productCode;

    @ApiModelProperty(value = "")
    private String productName;

    @ApiModelProperty(value = "")
    private String mouldCode;

    @ApiModelProperty(value = "")
    private String mouldName;

    @ApiModelProperty(value = "")
    private String owner;

    @ApiModelProperty(value = "")
    private String number;

    @ApiModelProperty(value = "")
    private String unit;

    @ApiModelProperty(value = "")
    private String location;

    @ApiModelProperty(value = "")
    private String type;

    @ApiModelProperty(value = "")
    private String remark;

}
