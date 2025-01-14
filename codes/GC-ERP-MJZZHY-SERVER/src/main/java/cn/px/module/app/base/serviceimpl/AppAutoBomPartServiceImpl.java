package cn.px.module.app.base.serviceimpl;

import cn.px.module.app.base.bean.AppAutoBomPart;
import cn.px.module.app.base.dao.AppAutoBomPartDao;
import cn.px.module.app.base.service.AppAutoBomPartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */

@Service
public class AppAutoBomPartServiceImpl extends ServiceImpl<AppAutoBomPartDao, AppAutoBomPart> implements AppAutoBomPartService {

    @Resource
    AppAutoBomPartDao autoBomPartDao;

    @Override
    public List<AppAutoBomPart> getList(Map<String,Object> conditions) {
        return autoBomPartDao.getList(conditions);
    }

    @Override
    public List<AppAutoBomPart> getListByBean(AppAutoBomPart autoBomPart) {
        return autoBomPartDao.getList(autoBomPart);
    }

    @Override
    public int getTotal(Map<String,Object> conditions) {
        return autoBomPartDao.getTotal(conditions);
    }

    @Override
    public List<AppAutoBomPart> getOldData() {
        return autoBomPartDao.getOldData();
    }
    @Override
    public int clear() {
        return autoBomPartDao.clear();
    }
}
