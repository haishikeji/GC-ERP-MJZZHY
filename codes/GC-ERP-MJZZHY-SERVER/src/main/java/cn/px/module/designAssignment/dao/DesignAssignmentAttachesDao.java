package cn.px.module.designAssignment.dao;

import cn.px.module.designAssignment.bean.DesignAssignmentAttaches;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author 品讯科技
 */
@Mapper
public interface DesignAssignmentAttachesDao extends BaseMapper<DesignAssignmentAttaches> {
    /**
     * 物理删除，根据条件删除
     * @param conditions 条件
     * @return int 执行成功的数量
     */
    Integer physicallyDelete(Map<String, Object> conditions);
}


