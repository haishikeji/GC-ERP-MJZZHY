package cn.px.module.contract.serviceimpl;

import java.util.Map;
import cn.px.module.contract.bean.ContractDetail;
import cn.px.module.contract.dao.ContractDetailDao;
import cn.px.module.contract.service.ContractDetailService;
import cn.px.utils.SnowflakeIdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @author 品讯科技
 */
@Service
public class ContractDetailServiceImpl extends ServiceImpl<ContractDetailDao, ContractDetail> implements ContractDetailService {
    @Resource
    ContractDetailDao contractDetailDao;
    @Resource
    SnowflakeIdWorker snowflakeIdWorker;

    @Override
    public Integer physicallyDelete(Map<String, Object> conditions) {
        if (conditions.isEmpty()) {
            return -1;
        }
        return contractDetailDao.physicallyDelete(conditions);
    }
}
