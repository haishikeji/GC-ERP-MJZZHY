package cn.px.module.contract.serviceimpl;

import java.util.Map;
import cn.px.module.contract.bean.ContractAttaches;
import cn.px.module.contract.dao.ContractAttachesDao;
import cn.px.module.contract.service.ContractAttachesService;
import cn.px.utils.SnowflakeIdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @author 品讯科技
 */
@Service
public class ContractAttachesServiceImpl extends ServiceImpl<ContractAttachesDao, ContractAttaches> implements ContractAttachesService {
    @Resource
    ContractAttachesDao contractAttachesDao;
    @Resource
    SnowflakeIdWorker snowflakeIdWorker;

    @Override
    public Integer physicallyDelete(Map<String, Object> conditions) {
        if (conditions.isEmpty()) {
            return -1;
        }
        return contractAttachesDao.physicallyDelete(conditions);
    }
}
