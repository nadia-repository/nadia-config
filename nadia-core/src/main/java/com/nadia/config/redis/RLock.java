package com.nadia.config.redis;

public interface RLock {


	/**
	 * 检查死锁
	 * @throws Exception
	 */
	void lock() throws Exception;

	void lock0() throws Exception;

	/**
	 * 检查死锁
	 * @param timeout
	 * @return
	 */
	boolean tryLock(long timeout);

	boolean tryLock0(long timeout);

	void uncLock();
}
