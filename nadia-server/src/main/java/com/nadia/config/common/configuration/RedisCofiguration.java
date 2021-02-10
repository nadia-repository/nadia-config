package com.nadia.config.common.configuration;

import com.nadia.config.publish.RedisPubSub;
import com.nadia.config.redis.ConfigCenterRedisService;
import com.nadia.config.redis.ConfigCenterRedisServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisCofiguration {

    private final static int database = 0;
    private final static int maxActive= 10;
    private final static int maxIdle = 8;
    private final static int minIdle =2;
    private final static long maxWait =100;

    @Bean("configCenterRedis")
    public RedisTemplate<String, Object> getRedisTemplate(RedisConnectionFactory configCenterRedisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(configCenterRedisConnectionFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setHashValueSerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setDefaultSerializer(stringRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean("configCenterRedisConnectionFactory")
    public RedisConnectionFactory getRedisConnectionFactory(@Value("${configcenter.redis.host}") String hostName,
                                                            @Value("${configcenter.redis.password}") String password,
                                                            @Value("${configcenter.redis.port}") int port) {
        JedisConnectionFactory jedisFactory = new JedisConnectionFactory();
        jedisFactory.setHostName(hostName);
        jedisFactory.setPort(port);
        jedisFactory.setPassword(password);
        jedisFactory.setDatabase(database);
        JedisPoolConfig poolConfig = new JedisPoolConfig(); // 进行连接池配置
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWait);
        jedisFactory.setPoolConfig(poolConfig);
        jedisFactory.afterPropertiesSet(); // 初始化连接池配置
        return jedisFactory;
    }

    @Bean
    public ConfigCenterRedisService getRedisService(){
        return new ConfigCenterRedisServiceImpl();
    }

//    @Bean("configCenterRedisListenerContainer")
//    public RedisMessageListenerContainer getRedisMessageListenerContainer(RedisTemplate configCenterRedis,
//                                                                          RedisConnectionFactory configCenterRedisConnectionFactory) {
//        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
//        redisMessageListenerContainer.setConnectionFactory(configCenterRedisConnectionFactory);
//        return redisMessageListenerContainer;
//    }

    @Bean
    public RedisPubSub gerRedisPubSub(){
        return new RedisPubSub();
    }
}
