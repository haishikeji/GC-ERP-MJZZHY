package cn.px.module.baseMaterial.controller;

import cn.px.module.general.bean.Result;
import cn.px.module.general.bean.ResultEnum;
import cn.px.module.baseMaterial.bean.BaseMaterial;
import cn.px.module.baseMaterial.service.BaseMaterialService;
import cn.px.utils.Rutils;
import cn.px.utils.SnowflakeIdWorker;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 品讯科技
 */
@RestController
@Api(tags = "BaseMaterial数据接口")
@RequestMapping(value = "/module/baseMaterial")
public class BaseMaterialController {

    @Resource
    HttpServletResponse response;
    @Resource
    BaseMaterialService baseMaterialService;
    @Resource
    SnowflakeIdWorker snowflakeIdWorker;

    @ApiOperation(value = "查询BaseMaterial", notes = "根据id查询BaseMaterial")
    @GetMapping("/getById/{id}")
    @ResponseBody
    public Result getById(@PathVariable Long id) {
        return Rutils.success(baseMaterialService.getById(id));
    }


    @ApiOperation(value = "带条件List 分页查询", notes = "带条件List 分页查询")
    @PostMapping("/getList")
    @ResponseBody
    public Result getList(@RequestBody @NotNull Map<String, Object> conditions) {
        QueryWrapper<BaseMaterial> baseMaterialQueryWrapper = new QueryWrapper<>();
        // 查询条件
        int pn = 1;
        int pageSize = 15;
        final String pnStr = "pn";
        final String pageSizeStr = "pageSize";
        // 查询条件
        final String key1 = "categoryId";
        final String key2 = "name";
        if (conditions.containsKey(pnStr) && conditions.get(pnStr) != "") {
            pn = Integer.parseInt(conditions.get(pnStr).toString());
        }
        if (conditions.containsKey(pageSizeStr) && conditions.get(pageSizeStr) != "") {
            pageSize = Integer.parseInt(conditions.get(pageSizeStr).toString());
        }
//        if (conditions.containsKey(key1) && conditions.get(key1) != "") {
//            baseMaterialQueryWrapper.eq("category_id", conditions.get(key1))
//                    .or()
//                    .apply("FIND_IN_SET('"+conditions.get(key1).toString()+"', category_ids)");
//        }
        if (conditions.containsKey(key1) && conditions.get(key1) != "") {
            baseMaterialQueryWrapper.eq("category_id", conditions.get(key1));
        }
        if (conditions.containsKey(key2) && conditions.get(key2) != "") {
            baseMaterialQueryWrapper.like("name", conditions.get(key2));
        }

        Page<BaseMaterial> page = new Page<>(pn, pageSize);
        List<BaseMaterial> baseMaterials = baseMaterialService.page(page, baseMaterialQueryWrapper).getRecords();
        baseMaterials = baseMaterials.stream().sorted(Comparator.comparing(BaseMaterial::getCreateTime).reversed()).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>(16);
        map.put("list", baseMaterials);
        map.put("total", baseMaterialService.count(baseMaterialQueryWrapper));
        return Rutils.success(map);
    }

    @ApiOperation(value = "删除BaseMaterial", notes = "根据id删除BaseMaterial")
    @DeleteMapping("/deleteById/{id}")
    @ResponseBody
    public Result deleteById(@PathVariable Long id) {
        try {
            if (baseMaterialService.removeById(id)) {
                return Rutils.success();
            } else {
                return Rutils.error();
            }
        } catch (Exception e) {
            return Rutils.serverError();
        }
    }

    @ApiOperation(value = "批量删除BaseMaterial", notes = "批量删除BaseMaterial")
    @PostMapping("/batchDelete")
    @ResponseBody
    public Result batchDelete(@RequestBody List<BaseMaterial> list) {
        try {
            if (baseMaterialService.removeBatchByIds(list)) {
                return Rutils.success();
            } else {
                return Rutils.error();
            }
        } catch (Exception e) {
            return Rutils.serverError();
        }
    }

    @ApiOperation(value = "整体保存", notes = "整体保存")
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public Result save(@RequestBody BaseMaterial model) {
        try {
            long count;
            if (StringUtils.isNotEmpty(model.getCode()) || StringUtils.isNotEmpty(model.getName())){
                 count = baseMaterialService.query()
                         .eq("code", model.getCode())
                         .ne("id", model.getId()==null?0:model.getId())
                         .ne("code","")
                        .count();
                if (count > 0) {
                    return Rutils.error(ResultEnum.NOT_SUCCESS.getCode(), "物料编码重复");
                }
                count = baseMaterialService.query()
                        .eq("name", model.getName())
                        .ne("id", model.getId()==null?0:model.getId())
                        .count();
                if (count > 0) {
                    return Rutils.error(ResultEnum.NOT_SUCCESS.getCode(), "物料名称重复");
                }
            }

            if (baseMaterialService.saveOrUpdate(model)) {
                return Rutils.success(model);
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
