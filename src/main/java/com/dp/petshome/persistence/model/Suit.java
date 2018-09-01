package com.dp.petshome.persistence.model;

import java.io.Serializable;
import java.util.Date;

public class Suit implements Serializable {
    private Integer id;

    private String name;

    private String description;

    private Integer price;

    private Integer score;

    private Integer people;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getPeople() {
        return people;
    }

    public void setPeople(Integer people) {
        this.people = people;
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
		return "Suit [id=" + id + ", name=" + name + ", description=" + description + ", price=" + price + ", score=" + score
				+ ", people=" + people + ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
	}
}