package com.nadia.config.common.rest;

import lombok.Data;

@Data
public class RestBody<T> extends RestHeader {
	private T data;

	public static <T> RestBody<T> succeed(T data) {
		RestBody<T> body = new RestBody<T>();
		body.setData(data);
		return body;
	}

	public static RestBody<String> succeed() {
		return new RestBody<String>();
	}

	public static RestBody<String> fail() {
		RestBody<String> body = new RestBody<String>();
		body.setErrorCode(1);
		body.setMsg("failed");
		return body;
	}

	public static RestBody<String> fail(long errorCode) {
		RestBody<String> body = new RestBody<String>();
		body.setErrorCode(errorCode);
		body.setMsg("failed");
		return body;
	}

	public static RestBody<String> fail(String msg) {
		return fail(1, msg);
	}

	public static RestBody<String> fail(long errorCode, String msg) {
		RestBody<String> body = new RestBody<String>();
		body.setErrorCode(errorCode);
		body.setMsg(msg);
		return body;
	}

}