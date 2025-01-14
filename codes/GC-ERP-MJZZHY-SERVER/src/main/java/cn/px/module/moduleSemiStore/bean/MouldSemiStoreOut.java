package cn.px.module.moduleSemiStore.bean;

import cn.px.module.general.bean.BaseBean;
import lombok.Data;
import java.sql.Timestamp;
import io.swagger.annotations.ApiModelProperty;
import lombok.experimental.Accessors;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableField;
import java.util.List;

/**
 * @author 品讯科技
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class MouldSemiStoreOut extends BaseBean {
    @ApiModelProperty(value = "")
    private String formNo;

    @ApiModelProperty(value = "")
    private Timestamp formDate;

    @ApiModelProperty(value = "")
    private String remark;

    @ApiModelProperty(value = "出库了")
    private Byte stocked;

    @ApiModelProperty(value = "从表mould_semi_store_out_detail")
    @TableField(exist = false)
    private List<MouldSemiStoreOutDetail> details;
}
