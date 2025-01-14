package cn.px.module.purchase.purchaseOrder.dao;

import cn.px.module.purchase.purchaseOrder.bean.PurchaseOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
@Mapper
public interface PurchaseOrderDetailDao extends BaseMapper<PurchaseOrderDetail> {

    /**
     * 带条件的查询
     *
     * @param conditions 条件
     * @return List 集合
     */
    List<PurchaseOrderDetail> getList(Map<String, Object> conditions);

    /**
     * 带条件的查询
     *
     * @param conditions 条件
     * @return List 集合
     */
    List<PurchaseOrderDetail> getListNoTicketReversal(Map<String, Object> conditions);

    /**
     * 根据Bean查询 列表
     *
     * @param purchaseOrderDetail 实体类对象
     * @return List 集合
     */
    List<PurchaseOrderDetail> getList(PurchaseOrderDetail purchaseOrderDetail);

    /**
     * 带条件的查询总数
     *
     * @param conditions 条件
     * @return int 查询总数量
     */
    int getTotal(Map<String, Object> conditions);

    /**
     * 带条件的查询总数
     *
     * @param conditions 条件
     * @return int 查询总数量
     */
    int getTotalNoTicketReversal(Map<String, Object> conditions);


    Integer physicallyDelete(Map<String, Object> conditions);
}


