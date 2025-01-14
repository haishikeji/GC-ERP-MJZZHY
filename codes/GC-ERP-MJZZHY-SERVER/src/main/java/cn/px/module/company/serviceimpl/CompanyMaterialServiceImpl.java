package cn.px.module.company.serviceimpl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import cn.px.module.company.dao.CompanyMaterialMapper;
import cn.px.module.company.bean.CompanyMaterial;
import cn.px.module.company.service.ICompanyMaterialService;

/**
 * 供应商材质Service业务层处理
 *
 * @author 品讯科技
 * @date 2024-08
 */
@Service
public class CompanyMaterialServiceImpl  extends ServiceImpl<CompanyMaterialMapper, CompanyMaterial> implements ICompanyMaterialService
{
    @Resource
    private CompanyMaterialMapper companyMaterialMapper;

    /**
     * 查询供应商材质列表
     *
     * @param companyMaterial 供应商材质
     * @return 供应商材质
     */
    @Override
    public List<CompanyMaterial> getList(CompanyMaterial companyMaterial)
    {
        return companyMaterialMapper.getList(companyMaterial);
    }

    /**
     * 物理删除
     * @param companyMaterial
     * @return 删除结果
     */
    @Override
    public int physicalDelete(CompanyMaterial companyMaterial){ return companyMaterialMapper.physicalDelete(companyMaterial); };

}
