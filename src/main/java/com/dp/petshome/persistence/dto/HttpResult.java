package com.dp.petshome.persistence.dto;

public class HttpResult<T> {
	/**
	 * 是否成功标识 0-失败 1-成功 2-異常 3-未登錄 4-餘額不足 5-已参加（活动等）
	 */
	private Integer status;
	/**
	 * 消息内容
	 */
	private String msg;
	/**
	 * 数据
	 */
	private T data;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
