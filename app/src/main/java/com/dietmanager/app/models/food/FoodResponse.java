package com.dietmanager.app.models.food;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FoodResponse{

	@SerializedName("lunch")
	private List<FoodItem> lunch;

	@SerializedName("breakfast")
	private List<FoodItem> breakfast;

	@SerializedName("dinner")
	private List<FoodItem> dinner;

	@SerializedName("snacks")
	private List<FoodItem> snacks;

	public List<FoodItem> getLunch(){
		return lunch;
	}
	public List<FoodItem> geSnacks(){
		return snacks;
	}

	public List<FoodItem> getBreakfast(){
		return breakfast;
	}

	public List<FoodItem> getDinner(){
		return dinner;
	}
}