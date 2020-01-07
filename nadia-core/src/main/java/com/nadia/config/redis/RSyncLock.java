package com.nadia.config.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class RSyncLock implements RLock {
	private static final Logger logger = LoggerFactory.getLogger(RSyncLock.class);
	private static final int RETRY_TIME = 200;// 轮询时间,ms
	private static final int DEFAULT_EXPIRE_TIME = 20;// 默认超时时间,秒
	private static final int DEFAULT_MAX_WAIT_TIME = 30;// 默认最大超时时间,秒(防止程序过多浪费性能)
	private static final TimeUnit UNIT = TimeUnit.SECONDS;
	private final RedisService redis;
	private final String lockKey;
	private final String lockValue = getRandomUID();

	// 参数
	private final int expireTime;
	private final int waitTime;

	public RSyncLock(String lockKey, RedisService redis) {
		this.lockKey = lockKey;
		this.expireTime = DEFAULT_EXPIRE_TIME;
		this.waitTime = DEFAULT_EXPIRE_TIME + 5;
		this.redis = redis;
	}

	
	/**
	 * 随机一个uuid的字符串
	 * 
	 * @return
	 */
	private String getRandomUID() {
		return UUID.randomUUID().toString().replace("-", "").toLowerCase();
	}

	public RSyncLock(String lockKey, int expireTime, RedisService redis) {
		this.lockKey = lockKey;
		if (expireTime <= 0) {
			this.expireTime = DEFAULT_EXPIRE_TIME;
			logger.debug("lockkey={}, expireTime={}", lockKey, expireTime);
		} else {
			this.expireTime = expireTime;
		}
		if (this.expireTime < DEFAULT_MAX_WAIT_TIME - 5) {
			this.waitTime = this.expireTime + 5;
		} else {
			this.waitTime = DEFAULT_MAX_WAIT_TIME;
		}
		this.redis = redis;
	}

	@Override
    public void lock() {
		lock(true);
	}

	@Override
    public void lock0() {
		lock(false);
	}

	@Override
    public boolean tryLock(long timeout) {
		return tryLock(timeout, true);
	}

	@Override
    public boolean tryLock0(long timeout) {
		return tryLock(timeout, false);
	}

	@Override
    public void uncLock() {
		redis.unlock(lockKey, lockValue);
	}

	/**
	 * 检查和修复锁
	 * 
	 */
	private void checkDeadLock() {
		// 获取不到锁
		long ttl = redis.ttl(lockKey);
		if (ttl == -1) {
			// 删除失败锁
			redis.del(lockKey);
		}
	}

	private void lock(boolean checkDeadLock) {
		try {
			long begin = System.nanoTime();
			do {
				logger.debug("lock key: " + lockKey);
				boolean succ = redis.lock(lockKey, lockValue, expireTime);
				if (succ) {
					logger.debug("get lock, key: " + lockKey + " , expire in " + expireTime + " seconds.");
					return;
				}
				Thread.sleep(RETRY_TIME);
				if (System.nanoTime() - begin >= UNIT.toNanos(waitTime)) {
					if (checkDeadLock) {
						checkDeadLock();
					}
					logger.warn("get lock expired, key: " + lockKey + " , expire in " + waitTime + " seconds.");
					throw new RuntimeException("try get Lock[" + lockKey + "] time out!");
				}
			} while (true);
		} catch (Exception e) {
			logger.error("[redis exception] try get lock exception!", e);
			uncLock();
			throw new RuntimeException("try get Lock fail");
		}
	}

	private boolean tryLock(long timeout, boolean checkDeadLock) {
		try {
			if (timeout > waitTime) {
				timeout = waitTime;
				logger.debug("lockkey = {], timeout set max, timeout={}", lockKey, timeout);
			}
			long begin = System.nanoTime();
			do {
				logger.debug("try lock key: " + lockKey);
				boolean succ = redis.tryLock(lockKey, lockValue, expireTime);
				// 不存在锁
				if (succ) {
					logger.debug("get lock, key: " + lockKey + " , expire in " + expireTime + " seconds.");
					return true;
				}
				if (timeout <= 0) {
					if (checkDeadLock) {
						checkDeadLock();
					}
					break;
				}
				Thread.sleep(RETRY_TIME);
				if (System.nanoTime() - begin >= UNIT.toNanos(timeout)) {
					if (checkDeadLock) {
						checkDeadLock();
					}
					logger.warn("get lock, key: " + lockKey + " , expire in " + timeout + " seconds.");
					return false;
				}
			} while (true);
		} catch (Exception e) {
			logger.error("[redis exception] try get lock exception!", e);
			uncLock();
		}
		return false;
	}
}
