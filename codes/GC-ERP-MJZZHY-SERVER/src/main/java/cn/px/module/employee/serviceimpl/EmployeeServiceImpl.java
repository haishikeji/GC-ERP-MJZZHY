package cn.px.module.employee.serviceimpl;

import java.util.Map;
import cn.px.module.employee.bean.Employee;
import cn.px.module.employee.dao.EmployeeDao;
import cn.px.module.employee.service.EmployeeService;
import cn.px.utils.SnowflakeIdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author 品讯科技
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeDao, Employee> implements EmployeeService {

    @Resource
    EmployeeDao employeeDao;
    @Resource
    SnowflakeIdWorker snowflakeIdWorker;

    @Override
    public List<Employee> getList(Map<String, Object> conditions) {
        return employeeDao.getList(conditions);
    }

    @Override
    public Integer getTotal(Map<String, Object> conditions) {
        return employeeDao.getTotal(conditions);
    }
}
