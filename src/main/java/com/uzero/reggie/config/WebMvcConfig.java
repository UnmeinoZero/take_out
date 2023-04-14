package com.uzero.reggie.config;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.uzero.reggie.common.JacksonObjectMapper;
import com.uzero.reggie.interceptor.LoginCheckInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author 千叶零
 * @version 1.0
 * create 2023-03-30  16:07:25
 */
@Slf4j
@Configuration //配置类
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;

    @Override  //注册拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        //  加载拦截器，填入拦截路径
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**") //拦截路径
                .excludePathPatterns("/backend/**",
                                    "/front/**"); //不拦截路径
    }

    /**
     * 扩展mvc框架的消息转换器
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器");
        //创建消息转换器
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用jackson将java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合
        converters.add(0, messageConverter);
    }
}