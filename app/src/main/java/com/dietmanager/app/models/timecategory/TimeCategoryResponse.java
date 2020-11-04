package com.dietmanager.app.models.timecategory;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TimeCategoryResponse implements Serializable {

	@SerializedName("timeCategory")
	private List<TimeCategoryItem> timeCategory;

	public List<TimeCategoryItem> getTimeCategory(){
		return timeCategory;
	}
}