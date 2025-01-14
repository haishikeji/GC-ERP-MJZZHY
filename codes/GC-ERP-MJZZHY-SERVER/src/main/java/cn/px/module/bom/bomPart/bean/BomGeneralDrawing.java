package cn.px.module.bom.bomPart.bean;

import cn.px.module.general.bean.BaseBean;
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
public class BomGeneralDrawing extends BaseBean {

    @ApiModelProperty(value = "订单主表id")
    private Long saleOrderId;
    @ApiModelProperty(value = "订单产品明细id")
    private Long saleOrderProductId;
    @ApiModelProperty(value = "订单产品对应的模具id")
    private Long saleOrderDetailId;
    @ApiModelProperty(value = "原始文件名")
    private String rawFileName;
    @ApiModelProperty(value = "重新定义文件名")
    private String fileName;
    @ApiModelProperty(value = "上传文件地址")
    private String fileUrl;
    @ApiModelProperty(value = "上传文件大小")
    private String fileSize;
    @ApiModelProperty(value = "文件类型")
    private String fileType;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "")
    private Integer sort;

    /** 虚拟字段，数据库中不存在 */
    @ApiModelProperty(value = "当前的数据字典的编辑状态")
    @TableField(exist = false)
    private Boolean editStatus = false;
}
