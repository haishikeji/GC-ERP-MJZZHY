package cn.px.module.auto.autoBom.service;

import cn.px.module.auto.autoBom.bean.AutoBomPartProcess;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
public interface AutoBomPartProcessService extends IService<AutoBomPartProcess> {

    /**
     * 带条件的查询
     *
     * @param conditions 条件
     * @return List 集合
     */
    List<AutoBomPartProcess> getList(Map<String, Object> conditions);

    /**
     * 物理删除，根据条件删除
     *
     * @param conditions 条件
     * @return int 执行成功的数量
     */
    Integer physicallyDelete(Map<String, Object> conditions);
}
