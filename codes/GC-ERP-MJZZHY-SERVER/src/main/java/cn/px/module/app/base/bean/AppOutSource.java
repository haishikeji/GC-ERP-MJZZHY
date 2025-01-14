package cn.px.module.app.base.bean;

import cn.px.module.general.bean.BaseBean;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AppOutSource extends BaseBean {


    @ApiModelProperty(value = "外协商")
    private Long companyId;


    @ApiModelProperty(value = "岗位id")
    private Long stationId;

    @ApiModelProperty(value = "员工id")
    private Long employeeId;

    @ApiModelProperty(value = "工序完成的回写")
    private Integer status;

    @ApiModelProperty(value = "完成时间")
    private Timestamp completeTime;

    @ApiModelProperty(value = "岗位名称")
    @TableField(exist = false)
    private String stationName;

    @ApiModelProperty(value = "员工名称")
    @TableField(exist = false)
    private String employeeName;

    @ApiModelProperty(value = "岗位名称")
    @TableField(exist = false)
    private String companyName;
}
