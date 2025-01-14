package cn.px.module.ticket.ticketReversalDetail.service;

import cn.px.module.ticket.ticketReversalDetail.bean.TicketReversalDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

 /**
  * @author 品讯科技
  */
 public interface TicketReversalDetailService extends IService<TicketReversalDetail> {

 /**
 * 根据Bean查询 列表
 * @param ticketReversalDetail 实体类对象
 * @return List 集合
 */
 List<TicketReversalDetail> getListByBean(TicketReversalDetail ticketReversalDetail);


 /**
 * 带条件的查询
 * @param conditions 条件
 * @return List 集合
 */
 List<TicketReversalDetail> getList(Map<String,Object> conditions);

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
 List<TicketReversalDetail> getOldData();

 /**
 * 清除当前表所有数据
 * @return 操作行数
 */
 int clear();
}
