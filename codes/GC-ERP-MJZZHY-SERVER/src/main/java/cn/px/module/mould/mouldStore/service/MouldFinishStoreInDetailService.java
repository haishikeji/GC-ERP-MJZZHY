package cn.px.module.mould.mouldStore.service;

import cn.px.module.mould.mouldStore.bean.MouldFinishStoreInDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface MouldFinishStoreInDetailService   extends IService<MouldFinishStoreInDetail> {
    List<MouldFinishStoreInDetail> getList(Map<String,Object> conditions);
    int getTotal(Map<String,Object> conditions);
    Integer physicallyDelete(Map<String, Object> conditions);
}
