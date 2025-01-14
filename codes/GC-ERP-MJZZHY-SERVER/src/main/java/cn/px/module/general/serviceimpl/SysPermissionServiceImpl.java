package cn.px.module.general.serviceimpl;

import cn.px.module.general.bean.SysPermission;
import cn.px.module.general.dao.SysPermissionDao;
import cn.px.module.general.service.SysPermissionService;
import cn.px.system.redis.RedisConstant;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionDao, SysPermission> implements SysPermissionService {
    @Resource
    SysPermissionDao sysPermissionDao;

    @Override
    @Cacheable(value = RedisConstant.PERMISSION, key = "'role_'+#conditions.id", condition = "#conditions!=null", unless = "#result == null")
    public List<SysPermission> selectPermissionByRoleId(Map<String, Object> conditions) {
        return sysPermissionDao.selectPermissionByRoleId(conditions);
    }

    @Override
    @Cacheable(value = RedisConstant.PERMISSION, key = "'role_'+#conditions.id", condition = "#conditions!=null", unless = "#result == null")
    public List<SysPermission> selectPermissionByRoleIds(Map<String, Object> conditions) {
        return sysPermissionDao.selectPermissionByRoleIds(conditions);
    }
}
