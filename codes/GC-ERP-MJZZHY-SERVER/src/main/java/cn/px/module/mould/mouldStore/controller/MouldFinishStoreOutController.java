package cn.px.module.mould.mouldStore.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import cn.px.module.general.bean.BaseBean;
import cn.px.module.general.bean.Result;
import cn.px.module.mould.mouldStore.bean.*;
import cn.px.module.mould.mouldStore.service.MouldFinishStoreOutDetailService;
import cn.px.module.mould.mouldStore.service.MouldFinishStoreService;
import cn.px.utils.JedisUtils;
import cn.px.utils.Rutils;
import cn.px.module.mould.mouldStore.service.MouldFinishStoreOutService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;
import java.util.stream.Collectors;

import cn.px.utils.CommonUtils;

import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 品讯科技
 */
@RestController
@Api(tags = "MouldFinishStoreOut数据接口")
@RequestMapping(value = "/module/mouldFinishStoreOut")
public class MouldFinishStoreOutController {

    @Resource
    JedisUtils jedisUtils;

    @Resource
    HttpServletResponse response;
    @Resource
    MouldFinishStoreOutService mouldFinishStoreOutService;

    @Resource
    MouldFinishStoreOutDetailService mouldFinishStoreOutDetailService;
    // @Resource
    // SnowflakeIdWorker snowflakeIdWorker;

    @ApiOperation(value = "查询MouldFinishStoreOut", notes = "根据id查询MouldFinishStoreOut")
    @GetMapping("/getById/{id}")
    @ResponseBody
    public Result getById(@PathVariable Long id) {
        Result result = null;
        try {
            Map<String, Object> map = new HashMap<>(16);
            if (id != null && id != 0) {
                // main
                MouldFinishStoreOut main = mouldFinishStoreOutService.getById(id);
                map.put("formData", main);
                if (main == null) {
                    throw new RuntimeException("未能根据订单id,获取主表记录");
                }


                // 俩个detail
                Map<String, Object> cond = new HashMap<>();
                cond.put("mouldFinishStoreOutId", id);
                List<MouldFinishStoreOutDetail> standard = mouldFinishStoreOutDetailService.getList(cond);
                main.setDetails(standard);
            }
            result = Rutils.success(map);
        } catch (RuntimeException ex) {
            result = Rutils.error(ex.getMessage());
        }
        return result;
    }

    @ApiOperation(value = "带条件List 分页查询", notes = "带条件List 分页查询")
    @PostMapping("/getList")
    @ResponseBody
    public Result getList(@RequestBody Map<String, Object> conditions) {
        Map<String, Object> map = new HashMap<>(16);
        int pn = 1;
        if (conditions.get("pn") != null) {
            pn = Integer.parseInt(conditions.get("pn").toString());
        }
        int size = 15;
        if (conditions.get("pageSize") != null) {
            size = Integer.parseInt(conditions.get("pageSize").toString());
        }
        conditions.put("start", (pn - 1) * size);
        //分页数据
        map.put("list", mouldFinishStoreOutService.getList(conditions));
        //总页数
        map.put("total", mouldFinishStoreOutService.getTotal(conditions));
        return Rutils.success(map);
    }


    @ApiOperation(value = "批量删除MouldFinishStoreOut", notes = "批量删除$MouldFinishStoreOut")
    @PostMapping("/batchDelete")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Result batchDelete(@RequestBody List<MouldFinishStoreOut> list) {
        Result result;

        if (list.size() == 0) {
            throw new RuntimeException("没有提供删除id");
        }
        // 检测，如果有入库的，进不能被删除了
        List<Long> ids = list.stream().map(BaseBean::getId).collect(Collectors.toList());
        QueryWrapper<MouldFinishStoreOutDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("mould_finish_store_out_id", ids);

        List<MouldFinishStoreOutDetail> detailList = mouldFinishStoreOutDetailService.list(queryWrapper);

        List<MouldFinishStoreOut> check = mouldFinishStoreOutService.listByIds(ids);
        for (MouldFinishStoreOut item : check) {
            if (item.getStocked() == 1) {
                return Rutils.error(item.getFormNo() + "已经出库,禁止删除!");
            }
        }
        // 主表删除
        mouldFinishStoreOutService.removeBatchByIds(list);

        // 从表删除
        mouldFinishStoreOutDetailService.remove(queryWrapper);

        result = Rutils.success();

        return result;
    }

    @ApiOperation(value = "整体保存", notes = "整体保存")
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public Result save(@RequestBody MouldFinishStoreOut mouldFinishStoreOut) {
        boolean changeFormNo = true;
        final String redisKey = "mouldFinishStoreOut_form_no";
        try {
            if (mouldFinishStoreOut.getFormNo() == null || mouldFinishStoreOut.getFormNo().isEmpty()) {
                // 自动生成单据号
                String formNo = CommonUtils.autoFormNo(jedisUtils, redisKey);
                mouldFinishStoreOut.setFormNo(formNo);
            }
            long count = mouldFinishStoreOutService.query()
                    .eq("form_no", mouldFinishStoreOut.getFormNo())
                    .ne("id", mouldFinishStoreOut.getId())
                    .count();
            if (count > 0) {
                changeFormNo = false;
                return Rutils.error("单据号重复");
            }
            boolean bool = mouldFinishStoreOutService.saveOrUpdate(mouldFinishStoreOut);

            if (bool) {
                // 先删除订单下的产品明细
                Map<String, Object> conditions = new HashMap<>(16);
                long mainId = mouldFinishStoreOut.getId();
                conditions.put("mould_finish_store_out_id", mainId);
                mouldFinishStoreOutDetailService.physicallyDelete(conditions);
                List<MouldFinishStoreOutDetail> details = mouldFinishStoreOut.getDetails();
                if (!details.isEmpty()) {
                    // 设置主表id
                    details.forEach(l -> l.setMouldFinishStoreOutId(mainId));
                    bool = mouldFinishStoreOutDetailService.saveBatch(details);
                }
            }

            if (bool) {
                return Rutils.success(mouldFinishStoreOut);
            } else {
                changeFormNo = false;
                return Rutils.error();
            }
        } catch (Exception e) {
            changeFormNo = false;
            return Rutils.serverError(e.getMessage());
        } finally {
            if (changeFormNo) {
                jedisUtils.set("mouldFinishStoreOut_form_no", mouldFinishStoreOut.getFormNo(), 0);
            }
        }
    }

    @ApiOperation(value = "标准件,加工件都是它", notes = "带条件List 分页查询")
    @PostMapping("/getDialogList")
    @ResponseBody
    public Result getDialogList(@RequestBody Map<String, Object> conditions) {
        Result result;
        try {
            int pn = conditions.get("pn") == null ? 1 : Integer.parseInt(conditions.get("pn").toString());
            int size = conditions.get("pageSize") == null ? 15 : Integer.parseInt(conditions.get("pageSize").toString());
            int start = (pn - 1) * size;
            conditions.put("start", start);


            Map<String, Object> map = new HashMap<>();
            List<MouldFinishStore> list = mouldFinishStoreService.getList(conditions);
            int total = mouldFinishStoreService.getTotal(conditions);

            map.put("list", list);
            map.put("total", total);

            result = Rutils.success(map);

        } catch (RuntimeException ex) {
            result = Rutils.error(ex.getMessage());
        }
        return result;
    }

    @ApiOperation(value = "获取没有入库的明细们", notes = "带条件List 分页查询")
    @PostMapping("/outstock")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Result outstock(@RequestBody MouldFinishStoreOut mouldFinishStoreOut) {
        Result result;

        List<MouldFinishStoreOutDetail> details = mouldFinishStoreOut.getDetails();
        if (details.isEmpty() || details.size() == 0) {
            return Rutils.error("没有提供入库的明细!");
        }
        if (mouldFinishStoreOut.getId() == 0 || mouldFinishStoreOut.getId() == null) {
            return Rutils.error("没有保存过的订单,不能出库!");
        }
        if (mouldFinishStoreOut.getStocked() != 0) {
            return Rutils.error("已出库的订单,不能再次出库!");
        }

        List<MouldFinishStore> target = new ArrayList<>();
        MouldFinishStore temp = null;
        for (MouldFinishStoreOutDetail item : details) {
            temp = new MouldFinishStore();
            temp.setId(item.getMouldFinishStoreId());
            temp.setOutStock((byte) 1);
            target.add(temp);
        }

        // 主表
        mouldFinishStoreService.saveOrUpdateBatch(target);

        // 回写
        mouldFinishStoreOut.setStocked((byte) 1);
        mouldFinishStoreOutService.saveOrUpdate(mouldFinishStoreOut);

        result = Rutils.success();
        return result;
    }

    @Resource
    MouldFinishStoreService mouldFinishStoreService;
}
