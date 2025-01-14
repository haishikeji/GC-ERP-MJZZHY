package cn.px.module.article.dao;

import cn.px.module.article.bean.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
@Mapper
public interface ArticleDao extends BaseMapper<Article> {
    /**
     * 带条件的查询
     * @param conditions 条件
     * @return List 集合
     */
    List<Article> getList(Map<String,Object> conditions);
    /**
     * 带条件的查询总数
     * @param conditions 条件
     * @return int 查询总数量
     */
    Integer getTotal(Map<String,Object> conditions);
}


