package cn.px.module.department.bean;

import cn.px.module.general.bean.BaseBean;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author 品讯科技
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class DepartmentEmployee extends BaseBean {

    @ApiModelProperty(value = "部门外键")
    private Long departmentId;

    @ApiModelProperty(value = "岗位外键")
    private Long stationId;

    @ApiModelProperty(value = "员工外键")
    private Long employeeId;

    @ApiModelProperty(value = "是否为负责人")
    private Boolean isLeader;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "备注")
    private String remark;

    /** 虚字段 **/
    @ApiModelProperty(value = "前端多动排序时候，需要的唯一id")
    @TableField(exist = false)
    private String uuid;

    @ApiModelProperty(value = "员工编号")
    @TableField(exist = false)
    private String employeeCode;

    @ApiModelProperty(value = "员工姓名")
    @TableField(exist = false)
    private String employeeName;

    @ApiModelProperty(value = "员工性别")
    @TableField(exist = false)
    private String employeeGender;

    @TableField(exist = false)
    private String departmentName;


    @ApiModelProperty(value = "岗位名称")
    @TableField(exist = false)
    private String stationName;
}
