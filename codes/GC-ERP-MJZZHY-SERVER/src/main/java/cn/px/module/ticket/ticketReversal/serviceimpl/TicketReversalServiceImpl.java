package cn.px.module.ticket.ticketReversal.serviceimpl;

import javax.annotation.Resource;
import cn.px.module.ticket.ticketReversal.bean.TicketReversal;
import cn.px.module.ticket.ticketReversal.dao.TicketReversalDao;
import cn.px.module.ticket.ticketReversal.service.TicketReversalService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */

@Service
public class TicketReversalServiceImpl extends ServiceImpl<TicketReversalDao, TicketReversal> implements TicketReversalService {

    @Resource
    TicketReversalDao ticketReversalDao;

    @Override
    public List<TicketReversal> getList(Map<String,Object> conditions) {
        return ticketReversalDao.getList(conditions);
    }

    @Override
    public List<TicketReversal> getListByBean(TicketReversal ticketReversal) {
        return ticketReversalDao.getList(ticketReversal);
    }

    @Override
    public int getTotal(Map<String,Object> conditions) {
        return ticketReversalDao.getTotal(conditions);
    }

    @Override
    public List<TicketReversal> getOldData() {
        return ticketReversalDao.getOldData();
    }
    @Override
    public int clear() {
        return ticketReversalDao.clear();
    }
}
