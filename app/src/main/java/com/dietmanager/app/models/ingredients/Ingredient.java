package com.dietmanager.app.models.ingredients;

import com.google.gson.annotations.SerializedName;

public class Ingredient {

	@SerializedName("dietitian_id")
	private Object dietitianId;

	@SerializedName("price")
	private String price;

	@SerializedName("name")
	private String name;

	@SerializedName("unit_type_id")
	private int unitTypeId;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("avatar")
	private Object avatar;

	@SerializedName("unit_type")
	private UnitType unitType;

	@SerializedName("status")
	private String status;

	public Object getDietitianId(){
		return dietitianId;
	}

	public String getPrice(){
		return price;
	}

	public String getName(){
		return name;
	}

	public int getUnitTypeId(){
		return unitTypeId;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public int getId(){
		return id;
	}

	public Object getAvatar(){
		return avatar;
	}

	public UnitType getUnitType(){
		return unitType;
	}

	public String getStatus(){
		return status;
	}
}