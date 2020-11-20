package com.dietmanager.app.models.ingredients;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnitType{

	@SerializedName("name")
	private String name;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("status")
	private String status;

	@SerializedName("code")
	@Expose
	private Object code;

	public String getName(){
		return name;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public int getId(){
		return id;
	}

	public String getStatus(){
		return status;
	}

	public Object getCode() {
		return code;
	}

	public void setCode(Object code) {
		this.code = code;
	}

}