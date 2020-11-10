package com.dietmanager.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Days {

	@SerializedName("name")
	private String name;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("status")
	private Date date;

	@SerializedName("day")
	private String day;

	public String getName(){
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public Date getDate() {
		return date;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public int getId(){
		return id;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
}