package cn.px.module.bom.bomPart.controller;

import cn.px.module.bom.bomPart.bean.BomPartAttaches;
import cn.px.module.general.bean.Result;
import cn.px.module.bom.bomPart.service.BomPartAttachesService;
import cn.px.utils.Rutils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 品讯科技
 */
@RestController
@Api(tags = "BomPartAttaches数据接口")
@RequestMapping(value = "/module/bomPartAttaches")
public class BomPartAttachesController {
    @Resource
    BomPartAttachesService bomPartAttachesService;

    @ApiOperation(value = "带条件List 分页查询", notes = "带条件List 分页查询")
    @PostMapping("/getList")
    @ResponseBody
    public Result getList(@RequestBody Map<String, Object> conditions) {
        Long bomPartId = conditions.get("bomPartId") == null ? 0 : Long.parseLong(conditions.get("bomPartId").toString());
        List<BomPartAttaches> bomPartAttaches = bomPartAttachesService.query()
                .eq("bom_part_id", bomPartId)
                .list();

        Map<String, Object> map = new HashMap<>(16);
        //分页数据
        map.put("list", bomPartAttaches);
        return Rutils.success(map);
    }

    @ApiOperation(value = "批量删除BomPartAttaches", notes = "批量删除BomPartAttaches")
    @PostMapping("/batchDelete")
    @ResponseBody
    public Result batchDelete(@RequestBody List<BomPartAttaches> list) {
        if (bomPartAttachesService.removeBatchByIds(list)) {
            return Rutils.success();
        } else {
            return Rutils.error();
        }
    }

    @ApiOperation(value = "整体保存", notes = "整体保存")
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public Result save(@RequestBody BomPartAttaches bomPartAttaches) {
        try {
            boolean bool = bomPartAttachesService.saveOrUpdate(bomPartAttaches);
            // 如果保存订单成功
            if (bool){
                return Rutils.success();
            } else {
                return Rutils.error();
            }
        } catch (Exception e) {
            // 2022-8-9 这么写，没毛病，
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Rutils.serverError();
        }
    }
}
