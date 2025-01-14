package cn.px.system.interceptor;

import cn.px.module.auto.autoBom.bean.AutoBomPart;
import cn.px.module.bom.bomPart.bean.BomPart;
import cn.px.module.bom.bomPartIndex.domain.BomPartIndex;
import cn.px.module.repairOrder.bean.RepairAssignmentDetailBomPart;
import cn.px.module.singleOrder.bean.SingleOrderDetail;
import cn.px.module.temporaryWorkHour.bean.TemporaryWorkHour;
import cn.px.utils.CommonUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 添加加工件 拦截器
 * 拦截 MyBatis 底层 insert 操作
 * @author 品讯科技
 */

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class BomPartInsertInterceptor implements Interceptor {
    private int count = 0;
    @Override
    public synchronized Object intercept(Invocation invocation) throws Throwable {
        Object result = null;
        List<String> bomMappers = new ArrayList<>();
        bomMappers.add("cn.px.module.bom.bomPart.dao.BomPartDao.insert");
        bomMappers.add("cn.px.module.auto.autoBom.dao.AutoBomPartDao.insert");
        bomMappers.add("cn.px.module.singleOrder.dao.SingleOrderDetailDao.insert");
        bomMappers.add("cn.px.module.repairOrder.dao.RepairAssignmentDetailBomPartDao.insert");
        bomMappers.add("cn.px.module.temporaryWorkHour.dao.TemporaryWorkHourDao.insert");
        List<Object> args = Arrays.asList(invocation.getArgs());
        MappedStatement mappedStatement = (MappedStatement) args.get(0);
        Object entity = args.get(1);
        if(bomMappers.contains(mappedStatement.getId())){
            result = invocation.proceed();
            System.out.println(entity.getClass().getName());
            BomPartIndex bomPartIndex = new BomPartIndex();
            switch (entity.getClass().getName()){
                case "cn.px.module.bom.bomPart.bean.BomPart":
                    BomPart bomPart = (BomPart) entity;
                    bomPartIndex.setTableName("bom_part");
                    bomPartIndex.setTableId(bomPart.getId());
                    break;
                case "cn.px.module.auto.autoBom.bean.AutoBomPart":
                    AutoBomPart autoBomPart = (AutoBomPart) entity;
                    bomPartIndex.setTableName("auto_bom_part");
                    bomPartIndex.setTableId(autoBomPart.getId());
                    break;
                case "cn.px.module.singleOrder.bean.SingleOrderDetail":
                    SingleOrderDetail singleOrderDetail = (SingleOrderDetail) entity;
                    bomPartIndex.setTableName("single_order_detail");
                    bomPartIndex.setTableId(singleOrderDetail.getId());
                    break;
                case "cn.px.module.repairOrder.bean.RepairAssignmentDetailBomPart":
                    RepairAssignmentDetailBomPart repairAssignmentDetailBomPart = (RepairAssignmentDetailBomPart) entity;
                    bomPartIndex.setTableName("repair_assignment_detail_bom_part");
                    bomPartIndex.setTableId(repairAssignmentDetailBomPart.getId());
                    break;
                case "cn.px.module.temporaryWorkHour.bean.TemporaryWorkHour":
                    TemporaryWorkHour temporaryWorkHour = (TemporaryWorkHour) entity;
                    bomPartIndex.setTableName("temporary_work_hour");
                    bomPartIndex.setTableId(temporaryWorkHour.getId());
                    break;
                default:
                    break;
            }
            bomPartIndex.setCreateTime(new Timestamp(System.currentTimeMillis()));
            bomPartIndex.setCreatorId(CommonUtils.getCurrentUser().getId());
            Executor executor = (Executor) invocation.getTarget();
            Long id = bomPartIndex.getByTableId(executor.getTransaction().getConnection());
            if(id==null) {
                bomPartIndex.insert(executor.getTransaction().getConnection());
            }
        }else {
            // 继续执行其他操作
            result = invocation.proceed();

        }
        return result;

    }

}
