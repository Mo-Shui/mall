package com.moshui.mall.config;

import com.moshui.mall.interceptor.AdminLoginInterceptor;
import com.moshui.mall.interceptor.MallCartNumberInterceptor;
import com.moshui.mall.interceptor.MallLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MallWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private AdminLoginInterceptor adminLoginInterceptor;

    @Autowired
    private MallLoginInterceptor mallLoginInterceptor;

    @Autowired
    private MallCartNumberInterceptor mallCartNumberInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加一个拦截器，拦截以/admin为前缀的url路径（后台登录拦截）
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login")
                .excludePathPatterns("/admin/coupling-test")
                .excludePathPatterns("/admin/categories/listForSelect")
                .excludePathPatterns("/admin/dist/**")
                .excludePathPatterns("/admin/plugins/**");
        // 商城页面登录拦截
        registry.addInterceptor(mallLoginInterceptor)
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .excludePathPatterns("/logout")
                .excludePathPatterns("/mall/**")
                .addPathPatterns("/goods/detail/**")
                .addPathPatterns("/shop-cart")
                .addPathPatterns("/shop-cart/**")
                .addPathPatterns("/saveOrder")
                .addPathPatterns("/orders")
                .addPathPatterns("/orders/**")
                .addPathPatterns("/personal")
                .addPathPatterns("/personal/updateInfo")
                .addPathPatterns("/selectPayType")
                .addPathPatterns("/payPage");
        // 购物车中的数量统一处理
        registry.addInterceptor(mallCartNumberInterceptor)
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .excludePathPatterns("/logout");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //文件拦截映射
        //将/upload路径的映射到D:\Workspace\NewIDEA\mall-image\
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:D:\\Workspace\\NewIDEA\\mall-image\\");
//        registry.addResourceHandler("/goods-img/**")
//                .addResourceLocations("file:D:\\Workspace\\NewIDEA\\mall-image\\");
    }
}
