package com.dietmanager.app.models.dietitiandetail;

import com.google.gson.annotations.SerializedName;

public class FollowersItem{

	@SerializedName("dietitian_id")
	private int dietitianId;

	@SerializedName("user_id")
	private int userId;

	@SerializedName("id")
	private int id;

	public void setDietitianId(int dietitianId){
		this.dietitianId = dietitianId;
	}

	public int getDietitianId(){
		return dietitianId;
	}

	public void setUserId(int userId){
		this.userId = userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}
}