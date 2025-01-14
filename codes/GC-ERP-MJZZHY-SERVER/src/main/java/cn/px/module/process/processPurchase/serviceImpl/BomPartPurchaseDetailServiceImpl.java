package cn.px.module.process.processPurchase.serviceImpl;
import cn.px.module.process.processPurchase.bean.BomPartPurchaseDetail;
import cn.px.module.process.processPurchase.dao.BomPartPurchaseDetailDao;
import cn.px.module.process.processPurchase.service.BomPartPurchaseDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BomPartPurchaseDetailServiceImpl extends ServiceImpl<BomPartPurchaseDetailDao, BomPartPurchaseDetail> implements BomPartPurchaseDetailService {
    @Autowired
    BomPartPurchaseDetailDao bomPartPurchaseDetailDao;

    @Override
    public List<BomPartPurchaseDetail> getDetailList(Map<String, Object> conditions) {
        return bomPartPurchaseDetailDao.getDetailList(conditions);
    }

    @Override
    public Integer getTotal(Map<String, Object> conditions) {
        return bomPartPurchaseDetailDao.getTotal(conditions);
    }

    @Override
    public Boolean update(BomPartPurchaseDetail temp) {
        return bomPartPurchaseDetailDao.update(temp);
    }

    @Override
    public List<BomPartPurchaseDetail> getDetailList2(BomPartPurchaseDetail temp) {
        return bomPartPurchaseDetailDao.getDetailList2(temp);
    }
}
