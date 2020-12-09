package com.dietmanager.app.models.dietitianlist;

import com.google.gson.annotations.SerializedName;

public class DietitianListItem{

	@SerializedName("flickr_link")
	private Object flickrLink;

	@SerializedName("gender")
	private Object gender;

	@SerializedName("twitter_link")
	private Object twitterLink;

	@SerializedName("latitude")
	private Object latitude;

	@SerializedName("rating")
	private String rating;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("description")
	private Object description;

	@SerializedName("device_type")
	private String deviceType;

	@SerializedName("experience")
	private int experience;

	@SerializedName("title")
	private String title="";

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("id")
	private int id;

	@SerializedName("email")
	private String email;

	@SerializedName("longitude")
	private Object longitude;

	@SerializedName("unique_id")
	private String uniqueId;

	@SerializedName("address")
	private String address="";

	@SerializedName("device_id")
	private String deviceId;

	@SerializedName("mobile")
	private String mobile;

	@SerializedName("wallet_balance")
	private String walletBalance;

	@SerializedName("otp")
	private String otp;

	@SerializedName("avatar")
	private String avatar="";

	@SerializedName("deleted_at")
	private Object deletedAt;

	@SerializedName("country_code")
	private Object countryCode;

	@SerializedName("dob")
	private Object dob;

	@SerializedName("device_token")
	private String deviceToken;

	@SerializedName("name")
	private String name;

	@SerializedName("fb_link")
	private Object fbLink;

	@SerializedName("status")
	private String status;

	public void setFlickrLink(Object flickrLink){
		this.flickrLink = flickrLink;
	}

	public Object getFlickrLink(){
		return flickrLink;
	}

	public void setGender(Object gender){
		this.gender = gender;
	}

	public Object getGender(){
		return gender;
	}

	public void setTwitterLink(Object twitterLink){
		this.twitterLink = twitterLink;
	}

	public Object getTwitterLink(){
		return twitterLink;
	}

	public void setLatitude(Object latitude){
		this.latitude = latitude;
	}

	public Object getLatitude(){
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

	public void setDescription(Object description){
		this.description = description;
	}

	public Object getDescription(){
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

	public void setLongitude(Object longitude){
		this.longitude = longitude;
	}

	public Object getLongitude(){
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

	public void setDob(Object dob){
		this.dob = dob;
	}

	public Object getDob(){
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

	public void setFbLink(Object fbLink){
		this.fbLink = fbLink;
	}

	public Object getFbLink(){
		return fbLink;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}