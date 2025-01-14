package cn.px.module.production.productionOutStore.bean;

import cn.px.module.general.bean.BaseBean;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ProductionOutStore extends BaseBean {
    private String formNo;
    private Timestamp formDate;
    private String storeOutType;
    private Long targetId;
    private String targetName;
    private String receiverId;
    private String remark;


    @TableField(exist = false)
    private String employeeName;
    /// 标准件的列表
    @TableField(exist = false)
    private List<ProductionOutStoreDetail> standard = new ArrayList<>();

    /// 原材料的列表
    @TableField(exist = false)
    private List<ProductionOutStoreDetail> material = new ArrayList<>();
}
