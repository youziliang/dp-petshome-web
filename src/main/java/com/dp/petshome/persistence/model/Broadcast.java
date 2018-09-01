package com.dp.petshome.persistence.model;

import java.io.Serializable;
import java.util.Date;

public class Broadcast implements Serializable {
    private Integer id;

    private String notice;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice == null ? null : notice.trim();
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
		return "Broadcast [id=" + id + ", notice=" + notice + ", createTime=" + createTime + ", updateTime="
				+ updateTime + "]";
	}
}