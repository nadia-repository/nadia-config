package com.nadia.config.spring;

import com.nadia.config.publish.RedisPubSub;
import com.nadia.config.redis.RedisService;
import com.nadia.config.redis.RedisServiceImpl;
import com.nadia.config.spi.ConfigCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

public class ConfigCenterRedisConfig implements ConfigCenter {

    private final static int database = 0;
    private final static int maxActive = 10;
    private final static int maxIdle = 8;
    private final static int minIdle = 2;
    private final static long maxWait = 100;

    @Bean("configCenterRedis")
    public RedisTemplate<String, Object> getRedisTemplate(@Value("${configcenter.redis.host}") String hostName,
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

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(jedisFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setHashValueSerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setDefaultSerializer(stringRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

//    @ConditionalOnMissingBean(RedisConnectionFactory.class)
//    @Bean("configCenterRedisConnectionFactory")
//    public RedisConnectionFactory getRedisConnectionFactory(@Value("${configcenter.redis.host}") String hostName,
//                                                            @Value("${configcenter.redis.password}") String password,
//                                                            @Value("${configcenter.redis.port}") int port) {
//        JedisConnectionFactory jedisFactory = new JedisConnectionFactory();
//        jedisFactory.setHostName(hostName);
//        jedisFactory.setPort(port);
//        jedisFactory.setPassword(password);
//        jedisFactory.setDatabase(database);
//        JedisPoolConfig poolConfig = new JedisPoolConfig(); // 进行连接池配置
//        poolConfig.setMaxTotal(maxActive);
//        poolConfig.setMaxIdle(maxIdle);
//        poolConfig.setMinIdle(minIdle);
//        poolConfig.setMaxWaitMillis(maxWait);
//        jedisFactory.setPoolConfig(poolConfig);
//        jedisFactory.afterPropertiesSet(); // 初始化连接池配置
//        return jedisFactory;
//    }

    @Bean("configCenterRedisListenerContainer")
    public RedisMessageListenerContainer getRedisMessageListenerContainer(@Value("${configcenter.redis.host}") String hostName,
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
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(jedisFactory);
        return redisMessageListenerContainer;
    }

    @Bean
    public RedisService getRedisService(){
        return new RedisServiceImpl();
    }

    @Bean
    public RedisPubSub gerRedisPubSub(){
        return new RedisPubSub();
    }
}
