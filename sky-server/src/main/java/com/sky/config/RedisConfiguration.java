package com.sky.config;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("开始创建redis模板");
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        //设置连接工厂对象
        template.setConnectionFactory(connectionFactory);
        //设置redis key序列化器
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }
}
