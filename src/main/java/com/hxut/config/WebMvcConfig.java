package com.hxut.config;

import com.hxut.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * description: WebMvcConfig静态资源放行
 * date: 2022/6/23 8:49
 * author: MR.孙
 */
@SuppressWarnings("all")
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * @description:  静态资源映射
     * @param registry
     * @return: void
     * @author: MR.孙
     * @date: 2022/6/23 9:21
    */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
                log.info("开始静态资源映射...");
                //放行backend目录静态资源
                registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
                //放行front目录静态资源
                registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");

    }

    /**
     * 扩展mvc框架的消息转换器
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0,messageConverter);
    }
}
