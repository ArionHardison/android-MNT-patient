package com.dietmanager.app.models.food;

import com.google.gson.annotations.SerializedName;

public class FoodItem {

	@SerializedName("dietitian_id")
	private Object dietitianId;

	@SerializedName("price")
	private String price;

	@SerializedName("time_category_id")
	private String timeCategoryId;

	@SerializedName("name")
	private String name;

	@SerializedName("description")
	private String description;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("days")
	private int days;

	@SerializedName("id")
	private int id;

	@SerializedName("avatar")
	private Object avatar;

	@SerializedName("status")
	private String status;

	@SerializedName("who")
	private String who;

	public Object getDietitianId(){
		return dietitianId;
	}

	public String getPrice(){
		return price;
	}

	public String getTimeCategoryId(){
		return timeCategoryId;
	}

	public String getName(){
		return name;
	}

	public String getDescription(){
		return description;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public int getDays(){
		return days;
	}

	public int getId(){
		return id;
	}

	public Object getAvatar(){
		return avatar;
	}

	public String getStatus(){
		return status;
	}

	public String getWho(){
		return who;
	}
}