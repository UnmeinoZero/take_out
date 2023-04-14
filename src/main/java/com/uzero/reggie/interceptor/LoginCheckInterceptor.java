package com.uzero.reggie.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.uzero.reggie.common.BaseContext;
import com.uzero.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-28  19:32:26
 */
@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override  //目标资源方法运行前运行，返回true:放行，返回false：不放行
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        //登录校验

        //获取请求url和session
        String url = req.getRequestURL().toString();
        log.info("请求url：{}", url);

        //网页端登录验证
        Long empId = (Long) req.getSession().getAttribute("employee");
        //判断session不为null或者为登录,短信请求放行
        if (!url.contains("/user")) {  //此判断为手机端请求时，拦截
            if (null != empId || url.contains("/employee/login")) {
                log.info("网页端用户已登录，用户id：{}", empId);
                //调用工具类存放用户id
                BaseContext.setCurrentId(empId);
                return true;
            }
        }

        //手机端登陆验证
        Long userId = (Long) req.getSession().getAttribute("user");
        //判断session不为null或者为登录,短信请求放行
        if (!url.contains("/employee")) {  //此判断为网页端请求时，拦截
            if (null != userId || url.contains("/user/login") || url.contains("/user/sendMsg")) {
                log.info("手机端用户已登录，用户id：{}", userId);
                //调用工具类存放用户id
                BaseContext.setCurrentId(userId);
                return true;
            }
        }

        //拦截
        log.info("账户未登录，拦截");
        R<String> err = R.error("NOTLOGIN");
        //手动转换 对象 -->> json
        String ontLogin = JSONObject.toJSONString(err);
        resp.getWriter().write(ontLogin);
        return false;
    }

    @Override //目标资源方法运行后运行
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override //视图渲染完毕后运行，最后运行
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
