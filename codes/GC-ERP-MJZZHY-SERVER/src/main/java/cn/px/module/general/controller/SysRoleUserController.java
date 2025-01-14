package cn.px.module.general.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import cn.px.module.general.bean.Result;
import cn.px.module.general.bean.SysRoleUser;
import cn.px.module.general.service.SysRoleUserService;
import cn.px.utils.Rutils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


/**
 * @author 品讯科技
 */
@RestController
@Api(tags = "SysRoleUser数据接口")
@RequestMapping(value = "/general/sysRoleUser")
public class SysRoleUserController {

    @Resource
    SysRoleUserService sysRoleUserService;

    @ApiOperation(value = "根据id查询",notes = "根据id查询")
    @GetMapping("/getById/{id}")
    @ResponseBody
    public Result getById(@PathVariable Long id){
        return Rutils.success(sysRoleUserService.getById(id));
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存",notes = "保存")
    @ResponseBody
    public Result save(@RequestBody SysRoleUser sysRoleUser){
        return (sysRoleUser.getId()==null?sysRoleUserService.save(sysRoleUser):sysRoleUserService.updateById(sysRoleUser))?Rutils.success():Rutils.error();
    }

    @PostMapping("/saveBatch")
    @ApiOperation(value = "批量保存",notes = "批量保存")
    @ResponseBody
    public Result saveBatch(@RequestBody List<SysRoleUser> list){
        return sysRoleUserService.saveOrUpdateBatch(list)?Rutils.success():Rutils.error();
    }

    @ApiOperation(value = "删除",notes = "删除")
    @GetMapping("/deleteById/{id}")
    @ResponseBody
    public Result deleteById(@PathVariable Long id){
        return sysRoleUserService.removeById(id)?Rutils.success():Rutils.error();
    }

    @ApiOperation(value = "批量删除",notes = "批量删除")
    @PostMapping("/batchDelete")
    @ResponseBody
    public Result batchDelete(@RequestBody List<Long> list){
        return sysRoleUserService.removeBatchByIds(list)?Rutils.success():Rutils.error();
    }

    /***
     * 2023-02-22 获取这个角色下的用户。
     * @param conditions
     * @return
     */
    @ApiOperation(value = "带条件List 分页查询",notes= "带条件List 分页查询")
    @PostMapping("/getList")
    @ResponseBody
    public Result getList(@RequestBody Map<String,Object> conditions){
        Map<String,Object> map = new HashMap<>(16);
        Integer pn = conditions.get("pn")==null?null:Integer.parseInt(conditions.get("pn").toString());
        Integer size =conditions.get("size")==null?null:Integer.parseInt(conditions.get("size").toString());
        Integer start = (pn==null||size==null)? null:(pn-1)*size;
    	conditions.put("start", start);
    	//分页数据
    	map.put("list", sysRoleUserService.getList(conditions));
    	if(start!=null){
            //总页数
            map.put("total",sysRoleUserService.getTotal(conditions));
    	}
        return Rutils.success(map);
    }


        @ApiOperation(value="迁移旧表数据",notes = "迁移旧表数据")
        @Transactional(rollbackFor=Exception.class)
        @GetMapping("sysRoleUser/copyData")
        @ResponseBody
        public Result copyData(){
        List<SysRoleUser> list = sysRoleUserService.getOldData();
            if(!list.isEmpty()){
            sysRoleUserService.clear();
            sysRoleUserService.saveBatch(list);
            return Rutils.success("操作成功！共迁移"+list.size()+"条数据");
            }
            return Rutils.error(500,"迁移失败");
        }



}
