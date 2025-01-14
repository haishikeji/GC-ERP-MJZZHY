package cn.px.system.interceptor;

import cn.px.module.general.bean.SysUser;
import cn.px.utils.CommonUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 品讯科技
 * 通过自定义拦截器，判断当前用户是否存在，不存在，则重新登录
 */
public class CustomInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SysUser currentSysUser = CommonUtils.getCurrentUser(response);
        return currentSysUser != null;
    }
}