package cn.px.module.company.dao;

import cn.px.module.company.bean.Company;
import cn.px.module.company.model.ProcessType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
@Mapper
public interface CompanyDao extends BaseMapper<Company> {
    /**
     * 带条件的查询
     *
     * @param conditions 条件
     * @return List 集合
     */
    List<Company> getList(Map<String, Object> conditions);

    /**
     * 带条件的查询总数
     *
     * @param conditions 条件
     * @return int 查询总数量
     */
    Integer getTotal(Map<String, Object> conditions);

    /***
     * 获取,这个供应商,能干的外协们
     * @param conditions
     * @return
     */
    ProcessType getSupplierProcess(Map<String, Object> conditions);

    List<ProcessType> getSupplierProcessList(Map<String, Object> conditions);
}


