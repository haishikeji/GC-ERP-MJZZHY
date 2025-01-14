package cn.px.module.finance.v1.dao;

import cn.px.module.finance.v1.bean.ReceivableDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

public interface ReceivableDetailDao extends BaseMapper<ReceivableDetail> {

    /**
     * 带条件的查询
     * @param conditions 条件
     * @return List 集合
     */
    List<ReceivableDetail> getList(Map<String,Object> conditions);

    /**
     * 带条件的查询总数
     * @param conditions 条件
     * @return int 查询总数量
     */
    int getTotal(Map<String,Object> conditions);

    Integer physicallyDelete(Map<String, Object> conditions);
}
