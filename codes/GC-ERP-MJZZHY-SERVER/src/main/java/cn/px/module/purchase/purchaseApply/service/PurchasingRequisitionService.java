package cn.px.module.purchase.purchaseApply.service;

import cn.px.module.purchase.purchaseApply.bean.PurchasingRequisition;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
public interface PurchasingRequisitionService extends IService<PurchasingRequisition> {

    /**
     * 根据Bean查询 列表
     *
     * @param purchasingRequisition 实体类对象
     * @return List 集合
     */
    List<PurchasingRequisition> getListByBean(PurchasingRequisition purchasingRequisition);


    /**
     * 带条件的查询
     *
     * @param conditions 条件
     * @return List 集合
     */
    List<PurchasingRequisition> getList(Map<String, Object> conditions);

    /**
     * 带条件的查询总数
     *
     * @param conditions 条件
     * @return int 查询总数量
     */
    int getTotal(Map<String, Object> conditions);

    /**
     * 获取就表数据
     *
     * @return 旧表数据集
     */
    List<PurchasingRequisition> getOldData();

    /**
     * 清除当前表所有数据
     *
     * @return 操作行数
     */
    int clear();
}
