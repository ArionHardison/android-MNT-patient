package com.dietmanager.app.models.timecategory;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TimeCategoryItem implements Serializable {

	@SerializedName("name")
	private String name;

	@SerializedName("created_at")
	private Object createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("status")
	private String status;

	public String getName(){
		return name;
	}

	public Object getCreatedAt(){
		return createdAt;
	}

	public int getId(){
		return id;
	}

	public String getStatus(){
		return status;
	}
}