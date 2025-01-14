package cn.px.module.mould.mouldStore.dao;

import org.apache.ibatis.annotations.Mapper;
import cn.px.module.mould.mouldStore.bean.MouldFinishStoreOut;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
@Mapper
public interface MouldFinishStoreOutDao extends BaseMapper<MouldFinishStoreOut> {

    /**
    * 物理删除，根据条件删除
    * @param conditions 条件
    * @return int 执行成功的数量
    */
    Integer physicallyDelete(Map<String, Object> conditions);

    /**
    * 带条件的查询
    * @param conditions 条件
    * @return List 集合
    */
    List<MouldFinishStoreOut> getList(Map<String,Object> conditions);

    /**
    * 带条件的查询总数
    * @param conditions 条件
    * @return int 查询总数量
    */
    int getTotal(Map<String,Object> conditions);
}


