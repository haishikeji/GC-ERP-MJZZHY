package cn.px.module.equipment.service;

import cn.px.module.equipment.bean.Equipment;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
public interface EquipmentService extends IService<Equipment> {
    /**
     * 带条件的查询
     * @param conditions 条件
     * @return List 集合
     */
    List<Equipment> getList(Map<String,Object> conditions);

    /**
     * 带条件的查询总数
     * @param conditions 条件
     * @return int 查询总数量
     */
    int getTotal(Map<String,Object> conditions);

}
