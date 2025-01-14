package cn.px.module.purchase.purchaseApply.dao;

import cn.px.module.purchase.purchaseApply.bean.PurchasingRequisitionDetail;
import cn.px.module.purchase.purchaseApply.model.PrePurchaseRequisitionDetail;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
@Mapper
public interface PurchasingRequisitionDetailDao extends BaseMapper<PurchasingRequisitionDetail> {

    /**
     * 带条件的查询
     *
     * @param conditions 条件
     * @return List 集合
     */
    List<PurchasingRequisitionDetail> getList(Map<String, Object> conditions);

    /**
     * 根据Bean查询 列表
     *
     * @param purchasingRequisitionDetail 实体类对象
     * @return List 集合
     */
    List<PurchasingRequisitionDetail> getList(PurchasingRequisitionDetail purchasingRequisitionDetail);

    /**
     * 带条件的查询总数
     *
     * @param conditions 条件
     * @return int 查询总数量
     */
    int getTotal(Map<String, Object> conditions);

    Integer physicallyDelete(Map<String, Object> conditions);


    List<PrePurchaseRequisitionDetail> getPrePurchaseApply(Map<String, Object> conditions);

    int getPrePurchaseApplyTotal(Map<String, Object> conditions);
}


