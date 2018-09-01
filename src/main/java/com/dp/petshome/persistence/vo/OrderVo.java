package com.dp.petshome.persistence.vo;

import java.io.Serializable;
import java.util.Date;

public class OrderVo implements Serializable {
	private String id;

	private String openid;

	private Long date;

	private Integer count;

	private String suitName;

	private String remark;

	private Integer status;

	private Date createTime;

	private Date updateTime;

	private String nickname;

	private String tel;
	
	private Integer role;

	private static final long serialVersionUID = 1L;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id == null ? null : id.trim();
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid == null ? null : openid.trim();
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getSuitName() {
		return suitName;
	}

	public void setSuitName(String suitName) {
		this.suitName = suitName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "OrderVo [id=" + id + ", openid=" + openid + ", date=" + date + ", count=" + count + ", suitName="
				+ suitName + ", remark=" + remark + ", status=" + status + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + ", nickname=" + nickname + ", tel=" + tel + ", role=" + role +"]";
	}
}