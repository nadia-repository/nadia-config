package com.nadia.config.common.security;

/**
 * @author: Wally.Wang
 * @date: 2019/12/06
 * @description:
 */
public class LockContext {

	final public static ThreadLocal<String> key = new ThreadLocal<>();

	public static void lock(String lockKey) {
		key.set(lockKey);
	}

	public static String getKey() {
		return key.get();
	}

	public static void clean() {
		key.remove();
	}

}
