package com.dietmanager.app.models.food;

import com.dietmanager.app.models.Dietitian;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FoodItem {

	@SerializedName("dietitian_id")
	private int dietitianId;

	@SerializedName("price")
	private String price;

	@SerializedName("time_category_id")
	private String timeCategoryId;

	@SerializedName("name")
	private String name;

	@SerializedName("dietitian")
	private Dietitian dietitian;

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

	@SerializedName("food_ingredients")
	@Expose
	private List<FoodIngredient> foodIngredients = null;

	public int getDietitianId(){
		return dietitianId;
	}

	public Dietitian getDietitian() {
		return dietitian;
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

	public List<FoodIngredient> getFoodIngredients() {
		return foodIngredients;
	}

	public void setFoodIngredients(List<FoodIngredient> foodIngredients) {
		this.foodIngredients = foodIngredients;
	}
}