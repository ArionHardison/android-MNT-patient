package com.dietmanager.app.models.food;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DietitianFood implements Serializable {


	@SerializedName("dietitian_id")
	private int dietitianId;

	@SerializedName("current")
	private int current;

	@SerializedName("category_id")
	private int categoryId;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("food_id")
	private int foodId;

	@SerializedName("day")
	private int day;

	@SerializedName("plan_id")
	private int planId;


	public void setDietitianId(int dietitianId){
		this.dietitianId = dietitianId;
	}

	public int getDietitianId(){
		return dietitianId;
	}

	public void setCurrent(int current){
		this.current = current;
	}

	public int getCurrent(){
		return current;
	}

	public void setCategoryId(int categoryId){
		this.categoryId = categoryId;
	}

	public int getCategoryId(){
		return categoryId;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setFoodId(int foodId){
		this.foodId = foodId;
	}

	public int getFoodId(){
		return foodId;
	}

	public void setDay(int day){
		this.day = day;
	}

	public int getDay(){
		return day;
	}

	public void setPlanId(int planId){
		this.planId = planId;
	}

	public int getPlanId(){
		return planId;
	}
}