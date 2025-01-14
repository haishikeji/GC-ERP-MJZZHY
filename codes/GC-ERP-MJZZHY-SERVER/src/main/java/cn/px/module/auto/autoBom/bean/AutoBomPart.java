package cn.px.module.auto.autoBom.bean;

import cn.px.module.general.bean.BaseBean;
import lombok.Data;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;

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
public class AutoBomPart extends BaseBean {

    @ApiModelProperty(value = "订单主键")
    private Long autoOrderId;

    @ApiModelProperty(value = "订单模具明细id")
    private Long autoOrderDetailId;

    @ApiModelProperty(value = "bom类别：标准件/加工件")
    private String type;

    @ApiModelProperty(value = "物料id")
    private Long baseMaterialId;

    @ApiModelProperty(value = "物料编码")
    private String baseMaterialCode;

    @ApiModelProperty(value = "物料名称")
    private String baseMaterialName;

    @ApiModelProperty(value = "加工件编码")
    private String partCode;

    @ApiModelProperty(value = "标准件名称/加工件名称")
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
    private BigDecimal density;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "数量")
    private Integer number;

    @ApiModelProperty(value = "总重")
    private BigDecimal totalWeight;

    @ApiModelProperty(value = "数量单位")
    private String unit;

    @ApiModelProperty(value = "是否热处理：否/是")
    private String heatTreatment;

    @ApiModelProperty(value = "排序")
    private String sort;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建工艺的员工id")
    private Long processAuthorId;

    /** 虚拟字段，数据库中不存在 */
    @ApiModelProperty(value = "任务书编号")
    @TableField(exist = false)
    private String designAssignmentNo;

    @ApiModelProperty(value = "订单号")
    @TableField(exist = false)
    private String autoOrderNo;


    @ApiModelProperty(value = "工序编码")
    @TableField(exist = false)
    private String dataDictionaryCode;

    @ApiModelProperty(value = "前端拖动排序用的uuid")
    @TableField(exist = false)
    private String uuid;

    /** 虚拟字段，数据库中不存在 */
    @ApiModelProperty(value = "当前的数据字典的编辑状态")
    @TableField(exist = false)
    private Boolean editStatus = false;

    @TableField(exist = false)
    private String productCode;
    @TableField(exist = false)
    private String productName;

    ///2022 7-14 补上，制定工艺的日期
    @TableField(exist = false)
    private String makeProcessDate;

    @ApiModelProperty(value = "Bom附件")
    @TableField(exist = false)
    private List<AutoBomPartAttaches> bomPartAttaches;

    @ApiModelProperty(value = "Bom工序")
    @TableField(exist = false)
    private List<AutoBomPartProcess> bomPartProcesses;

    /**
     * 2022-6-6 在此处修改，为了通杀所有位置计算单重，总重
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
        BigDecimal widht;
        BigDecimal length;
        BigDecimal weight;
        BigDecimal totalWeight;
        BigDecimal density = new BigDecimal("7.85");
        int number;
        thick = this.getThickness() == null ? new BigDecimal(0) : this.getThickness();
        widht = this.getWidth() == null ? new BigDecimal(0) : this.getWidth();
        length = this.getLength() == null ? new BigDecimal(0) : this.getLength();
        number = this.getNumber() == null ? 1 : this.getNumber();
        // 圆棒
        if (thick.equals(new BigDecimal("0"))) {
            int a = 1;
            // temp = (((item.rough_width / 2) ** 2 * 3.14 * item.rough_length * item.steel_density) / 1000000).toFixedPlus(2)
            weight = width.divide(new BigDecimal("2")).pow(2).multiply(new BigDecimal("3.14")).multiply(length).multiply(density).divide(new BigDecimal(1000000));
        } else {
            // temp = (item.rough_thick * item.rough_width * item.rough_length) / 1000000 * item.steel_density
            weight = thick.multiply(widht).multiply(length).divide(new BigDecimal(1000000)).multiply(density);
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

    // 2022-7-6 神通的序号，会读取导1.0的情况，不要导入这个样的数据。只导入1就可以了
    public void setSort(String v_sort) {
        if (v_sort.isEmpty()) {
            this.sort = "";
            return;
        }
        int index = 0;
        index = v_sort.indexOf(".");
        String result = "";
        if (index != -1 && index != 0) {
            result = v_sort.substring(0, index);
        } else {
            result = v_sort;
        }
        this.sort = result;
    }

}
