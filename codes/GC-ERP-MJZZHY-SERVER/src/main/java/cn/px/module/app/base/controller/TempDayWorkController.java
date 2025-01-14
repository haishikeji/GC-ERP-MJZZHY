package cn.px.module.app.base.controller;


import cn.px.module.general.bean.Result;
import cn.px.module.general.bean.SysUser;
import cn.px.module.app.base.dao.AppSettingDao;
import cn.px.module.app.base.dao.AppTempDayWorkDao;
import cn.px.module.app.base.model.AppTempDayWorkModel;
import cn.px.module.bom.bomPartIndex.domain.BomPartIndex;
import cn.px.module.bom.bomPartIndex.service.IBomPartIndexService;
import cn.px.module.temporaryWorkHour.bean.TemporaryWorkHour;
import cn.px.module.temporaryWorkHour.service.TemporaryWorkHourDetailService;
import cn.px.module.temporaryWorkHour.service.TemporaryWorkHourService;
import cn.px.utils.CommonUtils;
import cn.px.utils.Rutils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/***
 *  2023-02-07 临时报工思路
 *
 * web：
 *  创建，是未开始
 *
 * 手机端：
 *  用户扫码，就是进行中。
 *  用户点击，就是完成。
 */
@RestController
@Api(tags = "数据接口")
@RequestMapping(value = "/app/TempDayWork")
public class TempDayWorkController {

    @Resource
    private TemporaryWorkHourDetailService temporaryWorkHourDetailService;

    @Resource
    private AppTempDayWorkDao appTempDayWorkDao;

    @Resource
    IBomPartIndexService bomPartIndexService;

    @ApiOperation(value = "带条件List 分页查询", notes = "带条件List 分页查询")
    @PostMapping("/getList")
    @ResponseBody
    public Result getList(@RequestBody Map<String, Object> conditions) {
        int pn = conditions.get("pn") == null ? 1 : Integer.parseInt(conditions.get("pn").toString());
        int size = conditions.get("pageSize") == null ? 15 : Integer.parseInt(conditions.get("pageSize").toString());
        int start = (pn - 1) * size;
        conditions.put("start", start);
        conditions.put("deleted", 0);
        //分页数据
        Map<String, Object> map = new HashMap<>(16);
        map.put("list", appTempDayWorkDao.getList(conditions));
        map.put("total", appTempDayWorkDao.getTotal(conditions));
        return Rutils.success(map);
    }

    @ApiOperation(value = "getById", notes = "根据id查询报工信息")
    @GetMapping("/getById/{temporaryWorkHourId}")
    @ResponseBody
    public Result getById(@PathVariable Long temporaryWorkHourId) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("temporaryWorkHourId", temporaryWorkHourId);
        AppTempDayWorkModel temp = appTempDayWorkDao.getDetail(conditions);
        Map<String, Object> map = new HashMap<>(16);
        map.put("formData", temp);
        return Rutils.success(map);
    }

    @ApiOperation(value = "删除Contract", notes = "根据id删除Contract")
    @DeleteMapping("/deleteById/{temporaryWorkHourId}")
    @ResponseBody
    public Result deleteById(@PathVariable Long temporaryWorkHourId) {


        TemporaryWorkHour temporaryWorkHour = temporaryWorkHourService.getById(temporaryWorkHourId);
        if(temporaryWorkHour == null){
            return Rutils.error("没找到临时报工记录。");
        }

        if(temporaryWorkHour.getStatus().equals("进行中")){

        }else{
            return Rutils.success("只能删除进行中的报工记录!");
        }

        temporaryWorkHour.setStatus("未开始");

        temporaryWorkHourService.saveOrUpdate(temporaryWorkHour);
        return Rutils.success();
    }


    @Resource
    private TemporaryWorkHourService temporaryWorkHourService;


    @Resource
    HttpServletResponse response;

    @ApiOperation(value = "getByProcessCardId", notes = "根据条形码增加报工信息")
    @GetMapping("/getByProcessCardId/{temporaryWorkHourId}")
    @ResponseBody
    public Result getByProcessCardId(@PathVariable Long temporaryWorkHourId) {

        // 传过来的有可能是短码
        BomPartIndex bomPartIndex = bomPartIndexService.getById(temporaryWorkHourId);
        if (bomPartIndex !=null) {
            temporaryWorkHourId = bomPartIndex.getTableId();
        }
        // 1： 不是自己的工艺卡，不能报工
        // 2： 已经完成的报工，不能再次报工

        TemporaryWorkHour temporaryWorkHour = null;
        temporaryWorkHour = temporaryWorkHourService.getById(temporaryWorkHourId);
        if (temporaryWorkHour == null) {
            return Rutils.error("没找到临时报工记录。");
        }

        if (temporaryWorkHour.getStatus().isEmpty() || temporaryWorkHour.getStatus().equals("未开始")) {

        } else {
            return Rutils.error("请扫描未开始的临时工艺卡!");
        }


        SysUser sysUser = CommonUtils.getCurrentUser(this.response);
        Long employeeId = sysUser.getEmployeeId();

        Long employeeId2 = temporaryWorkHour.getEmployeeId();

        if (!employeeId.equals(employeeId2)) {
            return Rutils.error("请扫码自己的工艺卡。");
        }

        //
//        QueryWrapper<TemporaryWorkHourDetail> temporaryWorkHourDetailQueryWrapper = new QueryWrapper<>();
//        temporaryWorkHourDetailQueryWrapper.eq("temporary_work_hour_id",temporaryWorkHour.getId());
//        temporaryWorkHourDetailQueryWrapper.eq("deleted",0);
//        temporaryWorkHourDetailQueryWrapper.in("status","已结束","工序完成","工件完成","未开始");
//        List<TemporaryWorkHourDetail> exists = temporaryWorkHourDetailService.list(temporaryWorkHourDetailQueryWrapper);
//        if(exists !=null && exists.size() >=1){
//            return Rutils.error("请扫描未开始的临时工艺卡!");
//        }


        Map<String, Object> conditions = new HashMap<>();
        conditions.put("temporaryWorkHourId", temporaryWorkHourId);
        AppTempDayWorkModel temp = appTempDayWorkDao.getScan(conditions);
        Map<String, Object> map = new HashMap<>(16);
        map.put("formData", temp);
        return Rutils.success(map);

    }

    @Resource
    AppSettingDao appSettingDao;

    @ApiOperation(value = "整体保存", notes = "整体保存")
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public Result save(@RequestBody TemporaryWorkHour todo) {

        if(todo.getStatus().equals("未开始")){
            todo.setStatus("进行中");
        }

        // 临时工时,没有开始,结束的时间。
        if ("进行中".equals(todo.getStatus())) {
            // 进行中的时候，看看有没有扫码

        } else if ("已结束".equals(todo.getStatus())) {

        } else {
            return Rutils.error("无效的状态!");
        }
        temporaryWorkHourService.saveOrUpdate(todo);

        return Rutils.success(todo);
    }
}
