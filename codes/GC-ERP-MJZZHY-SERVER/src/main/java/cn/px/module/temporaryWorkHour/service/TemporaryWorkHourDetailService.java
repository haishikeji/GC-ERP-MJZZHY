package cn.px.module.temporaryWorkHour.service;

import cn.px.module.temporaryWorkHour.bean.TemporaryWorkHourDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface TemporaryWorkHourDetailService extends IService<TemporaryWorkHourDetail> {

    /**
     * 带条件的查询
     * @param conditions 条件
     * @return List 集合
     */
    List<TemporaryWorkHourDetail> getList(Map<String,Object> conditions);

    /**
     * 带条件的查询总数
     * @param conditions 条件
     * @return int 查询总数量
     */
    Integer getTotal(Map<String,Object> conditions);
}
