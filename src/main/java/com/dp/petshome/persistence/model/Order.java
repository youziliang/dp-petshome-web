package com.dp.petshome.persistence.model;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {
    private String id;

    private String openid;

    private Long date;

    private Integer count;

    private Integer suitId;

    private Boolean payment;

    private String remark;

    private Integer status;

    private Date createTime;

    private Date updateTime;

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

    public Integer getSuitId() {
        return suitId;
    }

    public void setSuitId(Integer suitId) {
        this.suitId = suitId;
    }

    public Boolean getPayment() {
        return payment;
    }

    public void setPayment(Boolean payment) {
        this.payment = payment;
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

	@Override
	public String toString() {
		return "Order [id=" + id + ", openid=" + openid + ", date=" + date + ", count=" + count + ", suitId=" + suitId + ", payment=" + payment + ", remark=" + remark + ", status="
				+ status + ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
	}
}