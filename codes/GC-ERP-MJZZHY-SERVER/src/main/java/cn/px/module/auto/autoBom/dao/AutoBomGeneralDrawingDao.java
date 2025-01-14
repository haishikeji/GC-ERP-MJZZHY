package cn.px.module.auto.autoBom.dao;

import org.apache.ibatis.annotations.Mapper;
import cn.px.module.auto.autoBom.bean.AutoBomGeneralDrawing;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
@Mapper
public interface AutoBomGeneralDrawingDao extends BaseMapper<AutoBomGeneralDrawing> {

    /**
     * 带条件的查询
     *
     * @param conditions 条件
     * @return List 集合
     */
    List<AutoBomGeneralDrawing> getList(Map<String, Object> conditions);

    /**
     * 根据Bean查询 列表
     *
     * @param autoBomGeneralDrawing 实体类对象
     * @return List 集合
     */
    List<AutoBomGeneralDrawing> getList(AutoBomGeneralDrawing autoBomGeneralDrawing);

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
    List<AutoBomGeneralDrawing> getOldData();

    /**
     * 清除当前表所有数据
     *
     * @return 操作行数
     */
    int clear();

}


