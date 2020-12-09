package com.dietmanager.app.models.dietitianlist;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DietitianListResponse{

	@SerializedName("dietitianList")
	private List<DietitianListItem> dietitianList;

	public void setDietitianList(List<DietitianListItem> dietitianList){
		this.dietitianList = dietitianList;
	}

	public List<DietitianListItem> getDietitianList(){
		return dietitianList;
	}
}