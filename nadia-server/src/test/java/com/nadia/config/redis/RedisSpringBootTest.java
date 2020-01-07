package com.nadia.config.redis;

import com.nadia.config.ServerBootStrapApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ServerBootStrapApplication.class)
public class RedisSpringBootTest {

    @Resource
    private RedisService redisService;

    @Test
    public void test() {
        System.out.println("hello world");
    }

    @Test
    public void test1() {
        redisService.delAll("*");
    }
}
