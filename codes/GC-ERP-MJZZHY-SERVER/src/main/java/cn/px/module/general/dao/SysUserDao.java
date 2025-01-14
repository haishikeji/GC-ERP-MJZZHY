package cn.px.module.general.dao;

import cn.px.module.general.bean.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
@Mapper
public interface SysUserDao extends BaseMapper<SysUser> {
    /**
     * 带条件的查询
     * @param conditions 条件
     * @return List 集合
     */
    List<SysUser> getList(Map<String,Object> conditions);
    /**
     * 带条件的查询总数
     * @param conditions 条件
     * @return int 总数
     */
    int getTotal(Map<String,Object> conditions);
}
