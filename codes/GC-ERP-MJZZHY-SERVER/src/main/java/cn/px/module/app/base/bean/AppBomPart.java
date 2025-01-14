package cn.px.module.app.base.bean;

import cn.px.module.general.bean.BaseBean;
import cn.px.module.bom.bomPart.bean.BomPartAttaches;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 品讯科技
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("bom_part")
public class AppBomPart extends BaseBean {

    @ApiModelProperty(value = "订单主表外键")
    private Long saleOrderId;

    @ApiModelProperty(value = "订单产品外键")
    private Long saleOrderProductId;

    @ApiModelProperty(value = "订单产品对应的模具明细id")
    private Long saleOrderDetailId;

    @ApiModelProperty(value = "类型（标准件/加工件）")
    private String type;

    @ApiModelProperty(value = "基础物料id")
    private Long baseMaterialId;

    @ApiModelProperty(value = "基础物料编码")
    private String baseMaterialCode;

    @ApiModelProperty(value = "基础物料名称")
    private String baseMaterialName;

    @ApiModelProperty(value = "BOM编码")
    private String partCode;

    @ApiModelProperty(value = "BOM名称")
    private String partName;

    @ApiModelProperty(value = "规格")
    private String standard;

    @ApiModelProperty(value = "长")
    private BigDecimal length;

    @ApiModelProperty(value = "宽")
    private BigDecimal width;

    @ApiModelProperty(value = "厚")
    private BigDecimal thickness;

    @ApiModelProperty(value = "加量长")
    private String addLength;

    @ApiModelProperty(value = "加量宽")
    private String addWidth;

    @ApiModelProperty(value = "加量厚")
    private String addThick;

    @ApiModelProperty(value = "密度")
    private String density;

    @ApiModelProperty(value = "单件重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "数量")
    private Integer number;

    @ApiModelProperty(value = "数量单位")
    private String unit;

    @ApiModelProperty(value = "总重")
    private BigDecimal totalWeight;

    @ApiModelProperty(value = "是否热处理")
    private String heatTreatment;

    // 2022-7-20 林，改了，由Interger改String
    @ApiModelProperty(value = "排序")
    private String sort;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建工艺的员工id")
    private Long processAuthorId;

    /**
     * 虚拟字段，数据库中不存在
     */
    @ApiModelProperty(value = "任务书编号")
    @TableField(exist = false)
    private String designAssignmentNo;

    @ApiModelProperty(value = "订单号")
    @TableField(exist = false)
    private String saleOrderNo;

    @ApiModelProperty(value = "模具号")
    @TableField(exist = false)
    private String mouldCode;

    @ApiModelProperty(value = "模具名")
    @TableField(exist = false)
    private String mouldName;

    @ApiModelProperty(value = "当前的数据字典的编辑状态")
    @TableField(exist = false)
    private Boolean editStatus = false;

    @ApiModelProperty(value = "Bom附件")
    @TableField(exist = false)
    private List<BomPartAttaches> bomPartAttaches;

    @ApiModelProperty(value = "Bom工序")
    @TableField(exist = false)
    private List<AppBomPartProcess> bomPartProcesses;


    /**
     * 2022-6-6 在此处修改，为了计算 加工的 单重，总重
     * 注意要忽略标准件
     * @param weight
     */
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    public BigDecimal getWeight() {
        // 不是加工件，就跳过吧
        if(!this.getType().equals("加工件")){
            return new BigDecimal(0);
        }
        BigDecimal result = new BigDecimal(0);

        BigDecimal thick;
        BigDecimal width;
        BigDecimal length;
        BigDecimal weight;
        BigDecimal totalWeight;
        BigDecimal density = new BigDecimal("7.85");
        int number;
        thick = this.getThickness() == null ? new BigDecimal(0) : this.getThickness();
        width = this.getWidth() == null ? new BigDecimal(0) : this.getWidth();
        length = this.getLength() == null ? new BigDecimal(0) : this.getLength();
        number = this.getNumber() == null ? 1 : this.getNumber();
        // 圆棒
        if (thick.equals(new BigDecimal("0"))) {
            int a = 1;
            // temp = (((item.rough_width / 2) ** 2 * 3.14 * item.rough_length * item.steel_density) / 1000000).toFixedPlus(2)
            weight = width.divide(new BigDecimal("2")).pow(2).multiply(new BigDecimal("3.14")).multiply(length).multiply(density).divide(new BigDecimal(1000000));
        } else {
            // temp = (item.rough_thick * item.rough_width * item.rough_length) / 1000000 * item.steel_density
            weight = thick.multiply(width).multiply(length).divide(new BigDecimal(1000000)).multiply(density);
        }
        result = weight.setScale(2, BigDecimal.ROUND_HALF_UP);
        return result;
    }
    public void setTotalWeight(BigDecimal totalWeight){
        this.totalWeight = totalWeight;
    }
    public BigDecimal getTotalWeight(){
        // 不是加工件，就跳过吧
        if(!this.getType().equals("加工件")){
            return new BigDecimal(0);
        }
        int number;
        number = this.getNumber() == null ? 1 : this.getNumber();
        BigDecimal weight = this.getWeight();
        BigDecimal totalWeight = weight.multiply(new BigDecimal((number)));
        totalWeight = totalWeight.setScale(2, BigDecimal.ROUND_HALF_UP);
        return totalWeight;
    }

}
