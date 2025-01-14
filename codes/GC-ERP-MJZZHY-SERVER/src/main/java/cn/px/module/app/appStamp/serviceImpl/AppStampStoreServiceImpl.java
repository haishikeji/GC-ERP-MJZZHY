package cn.px.module.app.appStamp.serviceImpl;

import cn.px.module.app.appStamp.bean.AppStampStore;
import cn.px.module.app.appStamp.dao.AppStampStoreDao;
import cn.px.module.app.appStamp.service.AppStampStoreService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AppStampStoreServiceImpl extends ServiceImpl<AppStampStoreDao, AppStampStore> implements AppStampStoreService {
}
