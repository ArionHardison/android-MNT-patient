package com.dietmanager.app.models.dietitiandetail;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DietitianDetailedResponse{

	@SerializedName("dietitian")
	private Dietitian dietitian;

	@SerializedName("subscription")
	private List<SubscriptionItem> subscription;

	@SerializedName("total_subscribers")
	private int totalSubscribers;

	public void setDietitian(Dietitian dietitian){
		this.dietitian = dietitian;
	}

	public Dietitian getDietitian(){
		return dietitian;
	}

	public void setSubscription(List<SubscriptionItem> subscription){
		this.subscription = subscription;
	}

	public List<SubscriptionItem> getSubscription(){
		return subscription;
	}

	public void setTotalSubscribers(int totalSubscribers){
		this.totalSubscribers = totalSubscribers;
	}

	public int getTotalSubscribers(){
		return totalSubscribers;
	}
}