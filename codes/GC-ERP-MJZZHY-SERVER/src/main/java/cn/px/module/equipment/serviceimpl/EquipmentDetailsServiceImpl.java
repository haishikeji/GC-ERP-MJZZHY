package cn.px.module.equipment.serviceimpl;

import cn.px.module.equipment.bean.EquipmentDetails;
import cn.px.module.equipment.dao.EquipmentDetailsDao;
import cn.px.module.equipment.service.EquipmentDetailsService;
import cn.px.utils.SnowflakeIdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
@Service
public class EquipmentDetailsServiceImpl extends ServiceImpl<EquipmentDetailsDao, EquipmentDetails> implements EquipmentDetailsService {

    @Resource
    EquipmentDetailsDao equipmentDetailsDao;
    @Resource
    SnowflakeIdWorker snowflakeIdWorker;

    @Override
    public List<EquipmentDetails> getList(Map<String, Object> conditions) {
        return equipmentDetailsDao.getList(conditions);
    }

    @Override
    public int getTotal(Map<String, Object> conditions) {
        return equipmentDetailsDao.getTotal(conditions);
    }

    @Override
    public Integer physicallyDelete(Map<String, Object> conditions) {
        if (conditions.isEmpty()) {
            return -1;
        }
        return equipmentDetailsDao.physicallyDelete(conditions);
    }
}
