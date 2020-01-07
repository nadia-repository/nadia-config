package com.nadia.config.redis;


public class TupleHelp {
	private String key;// 对应zset的value值
	private double value;// 对应zset的score

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
