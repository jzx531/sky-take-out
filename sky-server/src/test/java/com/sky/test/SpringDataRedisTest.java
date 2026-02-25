package com.sky.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.HashOperations;

import java.util.concurrent.TimeUnit;

/**
 * Spring Data Redis 测试类
 * 用于验证 Redis 连接及基本操作是否正常
 */
//@SpringBootTest // 1. 启动 Spring 容器，自动加载 application.yml 中的 Redis 配置
public class SpringDataRedisTest {

    // 2. 注入 RedisTemplate (Spring 操作 Redis 的核心工具类)
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 测试 1：连通性测试 (Ping)
     * 验证是否能成功连接 Redis 服务器
     */
    @Test
    public void testConnection() {
        // opsForValue() 获取操作 String 类型的接口
        ValueOperations valueOperations = redisTemplate.opsForValue();

        String key = "sky:test:connection";
        String value = "connected";

        // 设置数据
        valueOperations.set(key, value);

        // 获取数据
        Object result = valueOperations.get(key);

        System.out.println("✅ 连接测试结果：" + result);
        // 清理数据
        redisTemplate.delete(key);
    }

    /**
     * 测试 2：字符串操作 (带过期时间)
     * 模拟缓存验证码、Token 等场景
     */
    @Test
    public void testStringOperation() throws InterruptedException {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        String key = "sky:test:code";
        String code = "8888";

        // 设置数据，并指定过期时间为 10 秒
        valueOperations.set(key, code, 10, TimeUnit.SECONDS);
        System.out.println("✅ 数据已写入，有效期 10 秒");

        // 立即读取
        System.out.println("✅ 立即读取：" + valueOperations.get(key));

        // 等待 11 秒，验证是否过期
        System.out.println("⏳ 等待 11 秒...");
        Thread.sleep(11000);

        Object expiredResult = valueOperations.get(key);
        if (expiredResult == null) {
            System.out.println("✅ 数据已正常过期（读取为 null）");
        } else {
            System.out.println("❌ 数据未过期：" + expiredResult);
        }
    }

    /**
     * 测试 3：哈希操作 (Hash)
     * 模拟缓存用户信息、菜品对象等场景 (Key-Field-Value)
     */
    @Test
    public void testHashOperation() {
        HashOperations hashOperations = redisTemplate.opsForHash();

        String key = "sky:user:1001";

        // 模拟用户对象字段
        hashOperations.put(key, "name", "张三");
        hashOperations.put(key, "age", "25");
        hashOperations.put(key, "phone", "13800138000");

        System.out.println("✅ 哈希数据已写入");

        // 获取单个字段
        Object name = hashOperations.get(key, "name");
        System.out.println("✅ 获取 name 字段：" + name);

        // 获取所有字段
        System.out.println("✅ 获取所有字段：" + hashOperations.entries(key));

        // 清理数据
        redisTemplate.delete(key);
    }

    /**
     * 测试 4：批量删除
     */
    @Test
    public void testDelete() {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        String key = "sky:test:del";
        valueOperations.set(key, "temp");

        System.out.println("删除前存在：" + (redisTemplate.hasKey(key) ? "是" : "否"));

        Boolean deleteResult = redisTemplate.delete(key);

        System.out.println("删除操作结果：" + deleteResult);
        System.out.println("删除后存在：" + (redisTemplate.hasKey(key) ? "是" : "否"));
    }
}