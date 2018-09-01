package com.dp.petshome.enums;

public enum OrderStatus {

	UNFINISHED(1, "请求失败"),

	FINISHED(2, "方法異常"),

	ALL(3, "未登錄");

	public Integer status;

	public String msg;

	OrderStatus(Integer status, String msg) {
		this.status = status;
		this.msg = msg;
	}
}
