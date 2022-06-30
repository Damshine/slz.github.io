package com.hxut.filter;

import com.alibaba.fastjson.JSON;
import com.hxut.common.BaseContext;
import com.hxut.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * description: LoginCheckFilter
 * date: 2022/6/23 17:41
 * author: MR.孙
 */
@SuppressWarnings("all")
@WebFilter(urlPatterns = "/*",filterName = "loginCheckFilter")
@Slf4j
public class LoginCheckFilter implements Filter {
    private static final AntPathMatcher PATH_MATCHE=new AntPathMatcher();//路径匹配
    /**
     * @description:  拦截请求
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @return: void
     * @author: MR.孙
     * @date: 2022/6/23 17:43
    */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("开始拦截请求...");
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);
        //定义不需要处理的请求路径
        String[] urls=new String[]{
                "/backend/**",
                "/front/**",
                "/employee/page",
                "/employee/login",
                "/employee/logout",
                "/common/**",
                "/user/login",
                "/user/sendMsg"
        };
        //2、判断本次请求是否需要处理
        //3、如果不需要处理，则直接放行
        if(check(urls,requestURI)){
            log.info("本次请求:{},不需要处理->放行",requestURI);
            filterChain.doFilter(request,response);
            return ;
        }
        //4、判断登录状态，如果已登录，则直接放行(登录后会有session的数据，退出后session销毁)
        if(request.getSession().getAttribute("employee")!=null){
            log.info("当前线程id:{}",Thread.currentThread().getId());
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("employee"));
            log.info("用户已登录:  用户id->{}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return ;
        }


        //4-1、判断移动端登录状态，如果已登录，则直接放行(登录后会有session的数据，退出后session销毁)
        if(request.getSession().getAttribute("user")!=null){
            log.info("当前线程id:{}",Thread.currentThread().getId());
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("user"));
            log.info("用户已登录:  用户id->{}",request.getSession().getAttribute("user"));
            filterChain.doFilter(request,response);
            return ;
        }

        log.info("用户未登录");
        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
        return ;
    }

    /**
     * @description:  路径匹配,检查本次请求是否需要放行
     * @param urls 放行数组
     * @param requestURI 当前请求路径
     * @return: java.lang.Boolean
     * @author: MR.孙
     * @date: 2022/6/23 18:48
    */
    public Boolean check(String[] urls,String requestURI){
        //路径匹配例子:  backend/index要和backend/**匹配
        for(String url:urls){
            Boolean flag = PATH_MATCHE.match(url, requestURI);
            if(flag){
                return true;
            }
        }
        return false;
    }
}
