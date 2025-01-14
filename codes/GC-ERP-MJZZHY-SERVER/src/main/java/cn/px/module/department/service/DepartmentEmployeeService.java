package cn.px.module.department.service;

import cn.px.module.department.bean.DepartmentEmployee;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
public interface DepartmentEmployeeService extends IService<DepartmentEmployee> {
    /**
     * 带条件的查询
     * @param conditions 条件
     * @return List 集合
     */
    List<DepartmentEmployee> getList(Map<String,Object> conditions);

    /**
     * 带条件的查询总数
     * @param conditions 条件
     * @return int 查询总数量
     */
    Integer getTotal(Map<String,Object> conditions);
    /**
     * 物理删除，根据条件删除
     * @param conditions 条件
     * @return int 执行成功的数量
     */
    Integer physicallyDelete(Map<String, Object> conditions);
}
