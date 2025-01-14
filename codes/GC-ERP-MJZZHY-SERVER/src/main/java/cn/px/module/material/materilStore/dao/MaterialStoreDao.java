package cn.px.module.material.materilStore.dao;

import cn.px.module.material.materilStore.bean.MaterialStore;
import cn.px.module.material.materilStore.model.StoreQueryList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
@Mapper
public interface MaterialStoreDao extends BaseMapper<MaterialStore> {

    /**
     * 带条件的查询
     *
     * @param conditions 条件
     * @return List 集合
     */
    List<StoreQueryList> getList(Map<String, Object> conditions);

    /**
     * 带条件的查询总数
     *
     * @param conditions 条件
     * @return int 查询总数量
     */
    int getTotal(Map<String, Object> conditions);

}


