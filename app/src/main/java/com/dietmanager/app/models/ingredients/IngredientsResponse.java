package com.dietmanager.app.models.ingredients;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IngredientsResponse{

	@SerializedName("ingredients")
	private List<Ingredient> ingredients;

	public List<Ingredient> getIngredients(){
		return ingredients;
	}
}