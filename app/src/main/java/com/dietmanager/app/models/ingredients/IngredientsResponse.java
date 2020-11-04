package com.dietmanager.app.models.ingredients;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IngredientsResponse{

	@SerializedName("ingredients")
	private List<IngredientsItem> ingredients;

	public List<IngredientsItem> getIngredients(){
		return ingredients;
	}
}