package net.zpavelocity.springboot.test.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MyGsonConfig {
/*    @Bean
    GsonHttpMessageConverter gsonHttpMessageConverter() {
        // 自己提供一个GsonHttpMessageConverter实例
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        GsonBuilder builder = new GsonBuilder();
        // 设置Gson解析日期格式
        builder.setDateFormat("yyyy-MM-dd hh:mm:ss");
        // 设置Gson解析时修饰符protected的字段被过滤掉
        builder.excludeFieldsWithModifiers(Modifier.PROTECTED);
        // 创建Gson对象放入GsonHttpMessageConverter的实例并返回converter
        Gson gson = builder.create();
        converter.setGson(gson);
        return converter;
    }*/
}



