package cn.px.module.general.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author 品讯科技
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class SysRole extends BaseBean {

    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "状态0为 可用 其他待拓展")
    private Byte status;

    @ApiModelProperty(value = "描述")
    private String remark;

    /** ****************** 虚拟字段 ******************* **/
    @ApiModelProperty(value = "行编辑状态")
    @TableField(exist = false)
    private Boolean editStatus = false;
}
