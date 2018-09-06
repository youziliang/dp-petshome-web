package com.dp.petshome.enums;

public enum OrderStatus {

	UNFINISHED(1, "请求失败"),

	FINISHED(2, "方法異常"),

	ALL(3, "未登錄");

	public Integer status;

	public String desc;

	OrderStatus(Integer status, String desc) {
		this.status = status;
		this.desc = desc;
	}
}
