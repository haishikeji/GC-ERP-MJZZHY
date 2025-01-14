package cn.px.module.auto.autoDesignAssignment.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import cn.px.module.general.bean.Result;
import cn.px.module.general.bean.ResultEnum;
import cn.px.module.general.bean.SysUser;
import cn.px.module.auto.autoDesignAssignment.bean.AutoDesignAssignment;
import cn.px.module.auto.autoOrder.bean.AutoOrderDetail;
import cn.px.module.auto.autoOrder.service.AutoOrderDetailService;
import cn.px.module.general.service.SysUserService;
import cn.px.utils.CommonUtils;
import cn.px.utils.JedisUtils;
import cn.px.utils.Rutils;
import cn.px.module.auto.autoDesignAssignment.service.AutoDesignAssignmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;


/**
 * @author 品讯科技
 */
@RestController
@Api(tags = "AutoDesignAssignment数据接口")
@RequestMapping(value = "/module/autoDesignAssignment")
public class AutoDesignAssignmentController {
    @Resource
    JedisUtils jedisUtils;

    @Resource
    AutoDesignAssignmentService autoDesignAssignmentService;

    @Resource
    SysUserService sysUserService;

    @Resource
    AutoOrderDetailService autoOrderDetailService;

    @ApiOperation(value = "根据id查询", notes = "根据id查询")
    @GetMapping("/getById/{id}")
    @ResponseBody
    public Result getById(@PathVariable Long id) {
        try {
            Map<String, Object> conditions = new HashMap<>(16);
            conditions.put("id", id);
            List<AutoDesignAssignment> autoDesignAssignments = autoDesignAssignmentService.getList(conditions);
            AutoDesignAssignment autoDesignAssignment = autoDesignAssignments.isEmpty() ? null : autoDesignAssignments.get(0);

            long autoOrderId = 0;
            if (autoDesignAssignment != null) {
                SysUser sysUser = sysUserService.getById(autoDesignAssignment.getCreatorId());
                autoDesignAssignment.setCreatorName(sysUser != null ? sysUser.getUserName() : "");
                autoOrderId = autoDesignAssignment.getAutoOrderId();
            }

            Map<String, Object> conditionsForProduct = new HashMap<>(16);
            conditionsForProduct.put("autoOrderId", autoOrderId);
            List<AutoOrderDetail> products = autoOrderDetailService.getList(conditionsForProduct);

            Map<String, Object> map = new HashMap<>(16);
            map.put("formData", autoDesignAssignment);
            map.put("products", products);

            return Rutils.success(map);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Rutils.serverError();
        }
    }

    @ApiOperation(value = "整体保存", notes = "整体保存")
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public Result save(@RequestBody AutoDesignAssignment autoDesignAssignment) {
        boolean changeFormNo = true;
        final String redisKey = "auto_design_assignment_form_no";
        try {
            if (autoDesignAssignment.getFormNo() == null || autoDesignAssignment.getFormNo().isEmpty()) {
                // 自动生成单据号
                String formNo = CommonUtils.autoFormNo(jedisUtils, redisKey);
                autoDesignAssignment.setFormNo(formNo);
            }
            long count = autoDesignAssignmentService.query()
                    .eq("form_no", autoDesignAssignment.getFormNo())
                    .ne("id", autoDesignAssignment.getId())
                    .count();

            if (count > 0) {
                changeFormNo = false;
                return Rutils.error("设计任务书编号重复");
            }
            boolean bool = autoDesignAssignmentService.saveOrUpdate(autoDesignAssignment);
            if (bool) {
                return Rutils.success(autoDesignAssignment);
            } else {
                return Rutils.error();
            }
        } catch (Exception e) {
            changeFormNo = false;
            // 2022-8-9 这么写，没毛病，
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Rutils.serverError((e.getMessage() == null || e.getMessage().isEmpty()) ? ResultEnum.SERVER_ERROR.getMessage() : e.getMessage());
        } finally {
            if (changeFormNo) {
                jedisUtils.set(redisKey, autoDesignAssignment.getFormNo(), 0);
            }
        }
    }


    @PostMapping("/saveBatch")
    @ApiOperation(value = "批量保存", notes = "批量保存")
    @ResponseBody
    public Result saveBatch(@RequestBody List<AutoDesignAssignment> list) {
        return autoDesignAssignmentService.saveOrUpdateBatch(list) ? Rutils.success() : Rutils.error();
    }

    @ApiOperation(value = "删除", notes = "删除")
    @GetMapping("/deleteById/{id}")
    @ResponseBody
    public Result deleteById(@PathVariable Long id) {
        return autoDesignAssignmentService.removeById(id) ? Rutils.success() : Rutils.error();
    }

    @ApiOperation(value = "批量删除", notes = "批量删除")
    @PostMapping("/batchDelete")
    @ResponseBody
    public Result batchDelete(@RequestBody List<AutoDesignAssignment> list) {
        try {
            if (autoDesignAssignmentService.removeByIds(list)) {
                return Rutils.success();
            } else {
                return Rutils.error();
            }
        } catch (Exception e) {
            return Rutils.serverError();
        }
    }

    @ApiOperation(value = "带条件List 分页查询", notes = "带条件List 分页查询")
    @PostMapping("/getList")
    @ResponseBody
    public Result getList(@RequestBody Map<String, Object> conditions) {
        Map<String, Object> map = new HashMap<>(16);
        Integer pn = conditions.get("pn") == null ? null : Integer.parseInt(conditions.get("pn").toString());
        Integer size = conditions.get("pageSize") == null ? null : Integer.parseInt(conditions.get("pageSize").toString());
        Integer start = (pn == null || size == null) ? null : (pn - 1) * size;
        conditions.put("start", start);
        //分页数据
        map.put("list", autoDesignAssignmentService.getList(conditions));
        if (start != null) {
            //总页数
            map.put("total", autoDesignAssignmentService.getTotal(conditions));
        }
        return Rutils.success(map);
    }


    @ApiOperation(value = "迁移旧表数据", notes = "迁移旧表数据")
    @Transactional(rollbackFor = Exception.class)
    @GetMapping("autoDesignAssignment/copyData")
    @ResponseBody
    public Result copyData() {
        List<AutoDesignAssignment> list = autoDesignAssignmentService.getOldData();
        if (!list.isEmpty()) {
            autoDesignAssignmentService.clear();
            autoDesignAssignmentService.saveBatch(list);
            return Rutils.success("操作成功！共迁移" + list.size() + "条数据");
        }
        return Rutils.error(500, "迁移失败");
    }
}
