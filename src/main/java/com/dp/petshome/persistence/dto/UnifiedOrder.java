package com.dp.petshome.persistence.dto;

public class UnifiedOrder {

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 订单金额
	 */
	private Integer amount;

	/**
	 * 商品描述
	 */
	private String commDesc;

	/**
	 * 商品详情
	 */
	private String commDetail;
	
	/**
	 * 下单用户的openid
	 */
	private String openid;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getCommDesc() {
		return commDesc;
	}

	public void setCommDesc(String commDesc) {
		this.commDesc = commDesc;
	}

	public String getCommDetail() {
		return commDetail;
	}

	public void setCommDetail(String commDetail) {
		this.commDetail = commDetail;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	@Override
	public String toString() {
		return "UnifiedOrder [orderNo=" + orderNo + ", amount=" + amount + ", commDesc=" + commDesc + ", commDetail=" + commDetail + ", openid=" + openid + "]";
	}

}
