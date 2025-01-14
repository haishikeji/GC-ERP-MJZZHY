package cn.px.module.auto.autoBom.service;

import cn.px.module.auto.autoBom.bean.AutoBomPartAttaches;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @author 品讯科技
 */
public interface AutoBomPartAttachesService extends IService<AutoBomPartAttaches> {

    /**
     * 物理删除，根据条件删除
     *
     * @param conditions 条件
     * @return int 执行成功的数量
     */
    Integer physicallyDelete(Map<String, Object> conditions);
}
