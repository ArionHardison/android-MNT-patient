package com.dietmanager.app.models.dietitiandetail;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Dietitian{

	@SerializedName("flickr_link")
	private String flickrLink;
	@SerializedName("followers")
	private List<FollowersItem> followers;

	public void setFollowers(List<FollowersItem> followers){
		this.followers = followers;
	}

	public List<FollowersItem> getFollowers(){
		return followers;
	}
	@SerializedName("gender")
	private String gender;

	@SerializedName("twitter_link")
	private String twitterLink;

	@SerializedName("latitude")
	private double latitude;

	@SerializedName("rating")
	private String rating;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("description")
	private String description="";

	@SerializedName("device_type")
	private String deviceType;

	@SerializedName("experience")
	private int experience;

	@SerializedName("title")
	private String title;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("id")
	private int id;

	@SerializedName("email")
	private String email;

	@SerializedName("longitude")
	private double longitude;

	@SerializedName("unique_id")
	private String uniqueId;

	@SerializedName("address")
	private String address;

	@SerializedName("device_id")
	private String deviceId;

	@SerializedName("mobile")
	private String mobile;

	@SerializedName("wallet_balance")
	private String walletBalance;

	@SerializedName("otp")
	private String otp;

	@SerializedName("avatar")
	private String avatar;

	@SerializedName("deleted_at")
	private Object deletedAt;

	@SerializedName("country_code")
	private Object countryCode;

	@SerializedName("dob")
	private String dob;

	@SerializedName("device_token")
	private String deviceToken;

	@SerializedName("name")
	private String name;

	@SerializedName("fb_link")
	private String fbLink;

	@SerializedName("status")
	private String status;

	public void setFlickrLink(String flickrLink){
		this.flickrLink = flickrLink;
	}

	public String getFlickrLink(){
		return flickrLink;
	}

	public void setGender(String gender){
		this.gender = gender;
	}

	public String getGender(){
		return gender;
	}

	public void setTwitterLink(String twitterLink){
		this.twitterLink = twitterLink;
	}

	public String getTwitterLink(){
		return twitterLink;
	}

	public void setLatitude(double latitude){
		this.latitude = latitude;
	}

	public double getLatitude(){
		return latitude;
	}

	public void setRating(String rating){
		this.rating = rating;
	}

	public String getRating(){
		return rating;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setDeviceType(String deviceType){
		this.deviceType = deviceType;
	}

	public String getDeviceType(){
		return deviceType;
	}

	public void setExperience(int experience){
		this.experience = experience;
	}

	public int getExperience(){
		return experience;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setLongitude(double longitude){
		this.longitude = longitude;
	}

	public double getLongitude(){
		return longitude;
	}

	public void setUniqueId(String uniqueId){
		this.uniqueId = uniqueId;
	}

	public String getUniqueId(){
		return uniqueId;
	}

	public void setAddress(String address){
		this.address = address;
	}

	public String getAddress(){
		return address;
	}

	public void setDeviceId(String deviceId){
		this.deviceId = deviceId;
	}

	public String getDeviceId(){
		return deviceId;
	}

	public void setMobile(String mobile){
		this.mobile = mobile;
	}

	public String getMobile(){
		return mobile;
	}

	public void setWalletBalance(String walletBalance){
		this.walletBalance = walletBalance;
	}

	public String getWalletBalance(){
		return walletBalance;
	}

	public void setOtp(String otp){
		this.otp = otp;
	}

	public String getOtp(){
		return otp;
	}

	public void setAvatar(String avatar){
		this.avatar = avatar;
	}

	public String getAvatar(){
		return avatar;
	}

	public void setDeletedAt(Object deletedAt){
		this.deletedAt = deletedAt;
	}

	public Object getDeletedAt(){
		return deletedAt;
	}

	public void setCountryCode(Object countryCode){
		this.countryCode = countryCode;
	}

	public Object getCountryCode(){
		return countryCode;
	}

	public void setDob(String dob){
		this.dob = dob;
	}

	public String getDob(){
		return dob;
	}

	public void setDeviceToken(String deviceToken){
		this.deviceToken = deviceToken;
	}

	public String getDeviceToken(){
		return deviceToken;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setFbLink(String fbLink){
		this.fbLink = fbLink;
	}

	public String getFbLink(){
		return fbLink;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}