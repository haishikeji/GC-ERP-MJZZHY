package cn.px.system.shiro;

import cn.px.module.general.bean.SysPermission;
import cn.px.module.general.bean.SysRole;
import cn.px.module.general.bean.SysUser;
import cn.px.module.general.service.SysPermissionService;
import cn.px.module.general.service.SysRoleService;
import cn.px.module.general.service.SysUserService;
import cn.px.utils.JsonUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.*;


/**
 * @author 品讯科技
 * 描述：
 * 登录认证规则
 */
public class CustomRealm extends AuthorizingRealm {
    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final SysPermissionService sysPermissionService;

    public CustomRealm(SysUserService sysUserService, SysRoleService sysRoleService, SysPermissionService sysPermissionService) {
        this.sysUserService = sysUserService;
        this.sysRoleService = sysRoleService;
        this.sysPermissionService = sysPermissionService;
    }

    /**
     * 权限认证方法
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Object obj = principalCollection.getPrimaryPrincipal();
        SysUser sysUser;
        if (obj instanceof SysUser) {
            sysUser = (SysUser) obj;
        } else {
            sysUser = JsonUtils.parse(JsonUtils.toJson(obj), SysUser.class);
        }
        Set<String> permissions = new HashSet<>();
        Set<String> roles = new HashSet<>();

        QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
        sysUserQueryWrapper.eq("user_name", sysUser.getUserName());
        SysUser user = sysUserService.getOne(sysUserQueryWrapper);
//		SysRole sysRole = sysRoleService.getById(user.getSysRoleId());
//
//		if (sysRole != null) {
//			roles.add(sysRole.getName());
//			Map<String, Object> conditions = new HashMap<>(16);
//			conditions.put("roleId", sysRole.getId());
//			List<SysPermission> sysPermissions = sysPermissionService.selectPermissionByRoleId(conditions);
//			for (SysPermission sysPermission : sysPermissions) {
//				if (sysPermission != null) {
//					permissions.add(sysPermission.getCode());
//				}
//			}
//		}
//
//		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//		info.setRoles(roles);
//		info.setStringPermissions(permissions);
//		return info;

        Map<String, Object> conditions = new HashMap<>(16);
        conditions.put("sysRoleIds", user.getSysRoleIds());
        List<SysRole> sysRoles = sysRoleService.getListByRoleIds(conditions);

        if (!sysRoles.isEmpty()) {
            sysRoles.forEach(sysRole -> {
                roles.add(sysRole.getName());
            });
            List<SysPermission> sysPermissions = sysPermissionService.selectPermissionByRoleIds(conditions);
            for (SysPermission syspermission : sysPermissions) {
                if (syspermission != null) {
                    permissions.add(syspermission.getCode());
                }
            }
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);
        info.setStringPermissions(permissions);
        return info;
    }

    /**
     * 认证方法
     * 参数：传递的用户名密码
     * authenticationToken.是登录时候输入的“用户名”和“密码”加密后的结果
     * 当在Controller中，调用
     * Subject subject = SecurityUtils.getSubject();
     * subject.login(upToken);
     * 时，会调用 自定义 Realm 中的 重写方法 doGetAuthenticationInfo
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 获取登录的用户名密码（token）
        UsernamePasswordToken upToken = (UsernamePasswordToken) authenticationToken;
        String username = upToken.getUsername();
        String password = new String(upToken.getPassword());

        QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
        sysUserQueryWrapper.eq("user_name", username);
        // 根据用户名查询数据库
        SysUser sysUser = sysUserService.getOne(sysUserQueryWrapper);

        // 判断用户名是否存在或密码是否一致
        if (sysUser != null && sysUser.getPassword().equals(password)) {
            // 如果一致，返回安全数据
            // 构造方法：安全数据，密码，realm域名
            return new SimpleAuthenticationInfo(sysUser, sysUser.getPassword(), this.getName());
        } else {
            throw new IncorrectCredentialsException();
        }
        // 不一致，返回null（抛出异常）
    }
}
