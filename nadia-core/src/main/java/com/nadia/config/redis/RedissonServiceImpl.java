package com.nadia.config.redis;

//import org.redisson.api.RBucket;
//import org.redisson.api.RKeys;
//import org.redisson.api.RedissonClient;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.*;
//import java.util.concurrent.TimeUnit;

//@Service
public class RedissonServiceImpl{

//    @Resource
//    private RedissonClient redissonClient;
//
//    @Override
//    public void delAll(String key) {
//        RKeys keys = redissonClient.getKeys();
//        _delAll(keys.getKeysByPattern(key));
//    }
//
//    @Override
//    public void del(String key) {
//        RBucket<Object> bucket = redissonClient.getBucket(key);
//        bucket.delete();
//    }
//
//    @Override
//    public void del(Collection<String> keys) {
//        _delAll(keys);
//    }
//
//    private void _delAll(Iterable<String> keysByPattern) {
//        keysByPattern.forEach(k -> {
//            RBucket<Object> bucket = redissonClient.getBucket(k);
//            bucket.delete();
//        });
//    }
//
//    @Override
//    public boolean expire(String key, long timeOut) {
//        RBucket<Object> bucket = redissonClient.getBucket(key);
//        return bucket.expire(timeOut, TimeUnit.MILLISECONDS);
//    }
//
//    @Override
//    public long ttl(String key) {
//        return 0;
//    }
//
//    @Override
//    public long incr(String key) {
//        return 0;
//    }
//
//    @Override
//    public long incrBy(String key, long increment) {
//        return 0;
//    }
//
//    @Override
//    public void set(String key, String value, long timeOut) {
//        RBucket<Object> bucket = redissonClient.getBucket(key);
//        bucket.set(value, timeOut, TimeUnit.MILLISECONDS);
//    }
//
//    @Override
//    public void set(String key, String value) {
//        RBucket<Object> bucket = redissonClient.getBucket(key);
//        bucket.set(value);
//    }
//
//    @Override
//    public void set(String key, byte[] value) {
//        RBucket<Object> bucket = redissonClient.getBucket(key);
//        bucket.set(value);
//    }
//
//    @Override
//    public void set(String key, byte[] value, long timeOut) {
//        RBucket<Object> bucket = redissonClient.getBucket(key);
//        bucket.set(value, timeOut, TimeUnit.MILLISECONDS);
//    }
//
//    @Override
//    public String getAndSet(String key, String value) {
//        RBucket<Object> bucket = redissonClient.getBucket(key);
//        return bucket.getAndSet(value).toString();
//    }
//
//    @Override
//    public String getAndSet(String key, String value, long timeOut) {
//        RBucket<Object> bucket = redissonClient.getBucket(key);
//        String oldValue = bucket.get().toString();
//        bucket.set(value, timeOut, TimeUnit.MILLISECONDS);
//        return oldValue;
//    }
//
//    @Override
//    public boolean setNX(String key, String value) {
//        return false;
//    }
//
//    @Override
//    public boolean setNX(String key, String value, long timeOut) {
//        return false;
//    }
//
//    @Override
//    public String get(String key) {
//        RBucket<Object> bucket = redissonClient.getBucket(key);
//        return bucket.get().toString();
//    }
//
//    @Override
//    public List<String> mget(Collection<String> keys) {
//        List<String> values = new ArrayList<>();
//        for (String key : keys) {
//            values.add(get(key));
//        }
//        return values;
//    }
//
//    @Override
//    public void mset(Map<String, String> keys) {
//        for (String key : keys.keySet()) {
//            set(key, keys.get(key));
//        }
//    }
//
//    @Override
//    public byte[] getBytes(String key) {
//        return get(key).getBytes();
//    }
//
//    @Override
//    public Set<String> keys(String pattern) {
//        Iterable<String> keysByPattern = redissonClient.getKeys().getKeysByPattern(pattern);
//        Set<String> result = new HashSet<>();
//        keysByPattern.forEach(k -> result.add(k));
//        return result;
//    }
//
//    @Override
//    public boolean exists(String key) {
//        return redissonClient.getKeys().countExists(key) > 0L;
//    }
//
//    @Override
//    public boolean exists(String key, String value) {
//        return get(key).equals(value);
//    }
//
//    @Override
//    public long dbSize() {
//        return 0;
//    }
//
//    @Override
//    public String ping() {
//        return null;
//    }
//
//    @Override
//    public boolean sadd(String key, String value) {
//        return false;
//    }
//
//    @Override
//    public String spop(String key) {
//        return null;
//    }
//
//    @Override
//    public boolean zadd(String key, String value, double score) {
//        return false;
//    }
//
//    @Override
//    public long lpush(String key, String value) {
//        return 0;
//    }
//
//    @Override
//    public long rpush(String key, String value) {
//        return 0;
//    }
//
//    @Override
//    public String lpop(String key) {
//        return null;
//    }
//
//    @Override
//    public String rpop(String key) {
//        return null;
//    }
//
//    @Override
//    public String hget(String key, String field) {
//        return null;
//    }
//
//    @Override
//    public boolean hset(String key, String field, String value) {
//        return false;
//    }
//
//    @Override
//    public void hmset(String key, Map<String, String> map, long timeOut) {
//
//    }
//
//    @Override
//    public void hmset(String key, Map<String, String> map) {
//
//    }
//
//    @Override
//    public List<String> hmget(String key, List<String> fields) {
//        return null;
//    }
//
//    @Override
//    public void hincrby(String key, String filed, int value) {
//
//    }
//
//    @Override
//    public void hincrbyIfExists(String key, String filed, int value) {
//
//    }
//
//    @Override
//    public Map<String, String> hgetAll(String key) {
//        return null;
//    }
//
//    @Override
//    public List<String> sort(String key) {
//        return null;
//    }
//
//    @Override
//    public long size(String key) {
//        return 0;
//    }
//
//    @Override
//    public List<String> toList(String key, long begin, long end) {
//        return null;
//    }
//
//    @Override
//    public Set<String> toSet(String key, long begin, long end) {
//        return null;
//    }
//
//    @Override
//    public List<String> toList(String key) {
//        return null;
//    }
//
//    @Override
//    public Set<String> toSet(String key) {
//        return null;
//    }
//
//    @Override
//    public boolean del(String key, String value) {
//        return false;
//    }
//
//    @Override
//    public List<String> zrange(String key, long begin, long end, Order order) {
//        return null;
//    }
//
//    @Override
//    public List<String> zrange(String key, long begin, long end) {
//        return null;
//    }
//
//    @Override
//    public List<TupleHelp> zrangeWithScores(String key, long begin, long end, Order order) {
//        return null;
//    }
//
//    @Override
//    public List<String> zrangeByScore(String key, long count, double min, double max, Order order) {
//        return null;
//    }
//
//    @Override
//    public double zscore(String key, String value) {
//        return 0;
//    }
//
//    @Override
//    public double zincrby(String key, String value, double addv) {
//        return 0;
//    }
//
//    @Override
//    public RLock getLock(String key) {
//        return null;
//    }
//
//    @Override
//    public RLock getLock(String key, int timeout) {
//        return null;
//    }
//
//    @Override
//    public boolean lock(String key, String value, long timeOut) {
//        return false;
//    }
//
//    @Override
//    public boolean tryLock(String key, String value, long timeOut) {
//        return false;
//    }
//
//    @Override
//    public boolean unlock(String key, String value) {
//        return false;
//    }
//
//    @Override
//    public Set<String> smembers(String key) {
//        return null;
//    }
//
//    @Override
//    public void publish(String topic, String message) {
//
//    }
}
