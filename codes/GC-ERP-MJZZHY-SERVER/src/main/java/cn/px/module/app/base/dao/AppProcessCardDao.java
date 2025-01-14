package cn.px.module.app.base.dao;

import cn.px.module.app.base.bean.AppProcessCard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppProcessCardDao extends BaseMapper<AppProcessCard> {

    String getOrderModuleName(Long id);
    String getOrderCustomer(Long id);

    String getAutoOrderCustomer(Long id);
    String getAutoOrderModuleName(Long id);

    String getSingleOrderCustomer(Long id);
    String getEmployeeNameById(Long id);
}
