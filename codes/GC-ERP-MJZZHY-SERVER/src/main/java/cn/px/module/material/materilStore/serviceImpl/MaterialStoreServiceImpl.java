package cn.px.module.material.materilStore.serviceImpl;

import javax.annotation.Resource;

import cn.px.module.material.materilStore.bean.MaterialStore;
import cn.px.module.material.materilStore.dao.MaterialStoreDao;
import cn.px.module.material.materilStore.model.StoreQueryList;
import cn.px.module.material.materilStore.service.MaterialStoreService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */

@Service
public class MaterialStoreServiceImpl extends ServiceImpl<MaterialStoreDao, MaterialStore> implements MaterialStoreService {

    @Resource
    MaterialStoreDao materialStoreDao;

    @Override
    public List<StoreQueryList> getList(Map<String, Object> conditions) {
        return materialStoreDao.getList(conditions);
    }

    @Override
    public int getTotal(Map<String, Object> conditions) {
        return materialStoreDao.getTotal(conditions);
    }

}
