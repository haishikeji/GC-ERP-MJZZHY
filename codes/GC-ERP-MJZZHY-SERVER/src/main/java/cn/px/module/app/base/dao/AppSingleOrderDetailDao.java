package cn.px.module.app.base.dao;

import cn.px.module.app.base.bean.AppSingleOrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AppSingleOrderDetailDao extends BaseMapper<AppSingleOrderDetail> {
    List<AppSingleOrderDetail> getList(Map<String, Object> conditions);
    Integer physicallyDelete(Map<String, Object> conditions);
    int getTotal(Map<String,Object> conditions);
}
