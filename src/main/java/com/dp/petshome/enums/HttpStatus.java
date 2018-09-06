package com.dp.petshome.enums;

public enum HttpStatus {

	SUCCESS(0, "请求成功"),

	FAIL(1, "请求失败"),

	EXCEPTION(2, "方法異常"),

	NOLOGIN(3, "未登錄"),

	NOBALANCE(4, "餘額不足"),

	HAVEDONE(5, "已完成");

	public Integer status;

	public String msg;

	HttpStatus(Integer status, String msg) {
		this.status = status;
		this.msg = msg;
	}
}
