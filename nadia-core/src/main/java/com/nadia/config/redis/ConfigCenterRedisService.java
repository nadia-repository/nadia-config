package com.nadia.config.redis;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface ConfigCenterRedisService {
    void delAll(String key);

    void del(String key);

    void del(Collection<String> keys);

    boolean expire(String key, long timeOut);

    long ttl(String key);

    long incr(String key);

    long incrBy(String key, long increment);

    void set(String key, String value, long timeOut);

    void set(String key, String value);

    void set(String key, byte[] value);

    void set(String key, byte[] value, long timeOut);

    String getAndSet(String key, String value);

    String getAndSet(String key, String value, long timeOut);

    boolean setNX(String key, String value);

    boolean setNX(String key, String value, long timeOut);

    String get(String key);

    List<String> mget(Collection<String> keys);

    void mset(Map<String, String> keys);

    byte[] getBytes(String key);

    Set<String> keys(String pattern);

    boolean exists(String key);

    boolean exists(String key, String value);

    long dbSize();

    String ping();


    boolean sadd(String key, String value);

    String spop(String key);

    boolean zadd(String key, String value, double score);

    long lpush(String key, String value);

    long rpush(String key, String value);

    String lpop(String key);

    List<String> bLPop(int timeout,String ... keys);

    String rpop(String key);

    String hget(String key, String field);

    boolean hset(String key, String field, String value);

    void hmset(String key, Map<String, String> map, long timeOut);

    void hmset(String key, Map<String, String> map);

    List<String> hmget(String key, List<String> fields);

    void hincrby(String key, String filed, int value);

    void hincrbyIfExists(String key, String filed, int value);

    Map<String, String> hgetAll(String key);

    List<String> sort(String key);

    long size(String key);

    List<String> toList(String key, long begin, long end);

    Set<String> toSet(String key, long begin, long end);

    List<String> toList(String key);

    Set<String> toSet(String key);

    boolean del(String key, String value);

    List<String> zrange(String key, long begin, long end, Order order);

    List<String> zrange(String key, long begin, long end);

    List<TupleHelp> zrangeWithScores(String key, long begin, long end, Order order);

    List<String> zrangeByScore(String key, long count, double min, double max, Order order);

    double zscore(String key, String value);

    double zincrby(String key, String value, double addv);

    RLock getLock(String key);

    RLock getLock(String key, int timeout);

    boolean lock(String key, String value, long timeOut);

    boolean tryLock(String key, String value, long timeOut);

    boolean unlock(String key, String value);
    
    Set<String> smembers(String key);

    void publish(String topic, String message);

    void unlock(boolean locked, String key);
}
