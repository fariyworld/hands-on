package com.mace.handson.redisApp;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * description: redis 实操主启动类
 * <br />
 * Created by mace on 14:44 2018/7/27.
 */
@SpringBootApplication
@Slf4j
public class Redis_8088_Application {

    public static void main(String[] args) {

        SpringApplication.run(Redis_8088_Application.class, args);
    }


    /**
     * description:SpringBoot实现FastJson解析json数据 统一设置时间戳转换
     *
     * 格式化日期格式
     * @JSONField(format = "yyyy-MM-dd HH:mm:ss")
     *
     * 不进行序列化
     * @JSONField(serialize = false)
     *
     * SerializerFeature属性
     *
     * <br /><br />
     * create by mace on 2018/5/27 19:41.
     * @param
     * @return: org.springframework.boot.autoconfigure.http.HttpMessageConverters
     */
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters(){

        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        FastJsonConfig fastJsonConfig = new FastJsonConfig();

        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,             // 格式化
                SerializerFeature.WriteDateUseDateFormat,   // 统一转换时间戳: yyyy-MM-dd HH:mm:ss
                SerializerFeature.WriteMapNullValue,        // 输出空置字段
                SerializerFeature.WriteNullListAsEmpty,     // list字段如果为null，输出为[]，而不是null
                SerializerFeature.WriteNullNumberAsZero,    // 数值字段如果为null，输出为0，而不是null
                SerializerFeature.WriteNullBooleanAsFalse,  // Boolean字段如果为null，输出为false，而不是null
                SerializerFeature.WriteNullStringAsEmpty    // 字符类型字段如果为null，输出为""，而不是null
        );
        fastConverter.setFastJsonConfig(fastJsonConfig);

        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        mediaTypes.add(new MediaType(MediaType.TEXT_PLAIN, Charset.forName("UTF-8")));
        mediaTypes.add(new MediaType(MediaType.TEXT_HTML, Charset.forName("UTF-8")));
        fastConverter.setSupportedMediaTypes(mediaTypes);

        HttpMessageConverter<?> converter = fastConverter;

        log.info("自定义 fastJsonHttpMessageConverter 加载完成");

        return new HttpMessageConverters(converter);
    }
}
