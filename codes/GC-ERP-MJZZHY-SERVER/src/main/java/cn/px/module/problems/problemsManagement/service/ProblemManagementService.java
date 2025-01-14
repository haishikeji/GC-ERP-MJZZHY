package cn.px.module.problems.problemsManagement.service;

import cn.px.module.problems.problemsManagement.bean.ProblemManagement;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

 /**
  * @author 品讯科技
  */
 public interface ProblemManagementService extends IService<ProblemManagement> {

 /**
 * 根据Bean查询 列表
 * @param problemManagement 实体类对象
 * @return List 集合
 */
 List<ProblemManagement> getListByBean(ProblemManagement problemManagement);


 /**
 * 带条件的查询
 * @param conditions 条件
 * @return List 集合
 */
 List<ProblemManagement> getList(Map<String,Object> conditions);

 /**
 * 带条件的查询总数
 * @param conditions 条件
 * @return int 查询总数量
 */
 int getTotal(Map<String,Object> conditions);

 /**
 * 获取就表数据
 * @return 旧表数据集
 */
 List<ProblemManagement> getOldData();

 /**
 * 清除当前表所有数据
 * @return 操作行数
 */
 int clear();
}
