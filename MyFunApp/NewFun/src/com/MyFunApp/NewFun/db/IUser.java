package com.MyFunApp.NewFun.db;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IUser {

	@Expose
	private Object location;
	@Expose
	private String age;
	@SerializedName("updated_at")
	@Expose
	private String updatedAt;
	@SerializedName("food_style")
	@Expose
	private String foodStyle;
	@Expose
	private String id;
	@SerializedName("phone_num")
	@Expose
	private String phoneNum;
	@SerializedName("profile_image_url")
	@Expose
	private String profileImageUrl;
	@SerializedName("food_category")
	@Expose
	private String foodCategory;
	@Expose
	private String quality;
	@Expose
	private Object city;
	@Expose
	private Object description;
	@Expose
	private String nickname;
	@Expose
	private String gender;
	@Expose
	private Object province;
	@SerializedName("head_image_url")
	@Expose
	private String headImageUrl;
	@SerializedName("created_at")
	@Expose
	private String createdAt;
	@Expose
	private String birthday;
	@Expose
	private String token;
	@Expose
	private Double lat;
	@Expose
	private Double lng;
	
	
	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public String getToken(){
		return token;
	}
	
	public void setToken(String token){
		this.token = token;
	}

	/**
	 * 
	 * @return The location
	 */
	public Object getLocation() {
		return location;
	}

	/**
	 * 
	 * @param location
	 *            The location
	 */
	public void setLocation(Object location) {
		this.location = location;
	}

	/**
	 * 
	 * @return The age
	 */
	public String getAge() {
		return age;
	}

	/**
	 * 
	 * @param age
	 *            The age
	 */
	public void setAge(String age) {
		this.age = age;
	}

	/**
	 * 
	 * @return The updatedAt
	 */
	public String getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * 
	 * @param updatedAt
	 *            The updated_at
	 */
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * 
	 * @return The foodStyle
	 */
	public String getFoodStyle() {
		return foodStyle;
	}

	/**
	 * 
	 * @param foodStyle
	 *            The food_style
	 */
	public void setFoodStyle(String foodStyle) {
		this.foodStyle = foodStyle;
	}

	/**
	 * 
	 * @return The id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 *            The id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return The phoneNum
	 */
	public String getPhoneNum() {
		return phoneNum;
	}

	/**
	 * 
	 * @param phoneNum
	 *            The phone_num
	 */
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	/**
	 * 
	 * @return The profileImageUrl
	 */
	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	/**
	 * 
	 * @param profileImageUrl
	 *            The profile_image_url
	 */
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	/**
	 * 
	 * @return The foodCategory
	 */
	public String getFoodCategory() {
		return foodCategory;
	}

	/**
	 * 
	 * @param foodCategory
	 *            The food_category
	 */
	public void setFoodCategory(String foodCategory) {
		this.foodCategory = foodCategory;
	}

	/**
	 * 
	 * @return The quality
	 */
	public String getQuality() {
		return quality;
	}

	/**
	 * 
	 * @param quality
	 *            The quality
	 */
	public void setQuality(String quality) {
		this.quality = quality;
	}

	/**
	 * 
	 * @return The city
	 */
	public Object getCity() {
		return city;
	}

	/**
	 * 
	 * @param city
	 *            The city
	 */
	public void setCity(Object city) {
		this.city = city;
	}

	/**
	 * 
	 * @return The description
	 */
	public Object getDescription() {
		return description;
	}

	/**
	 * 
	 * @param description
	 *            The description
	 */
	public void setDescription(Object description) {
		this.description = description;
	}

	/**
	 * 
	 * @return The nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * 
	 * @param nickname
	 *            The nickname
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * 
	 * @return The gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * 
	 * @param gender
	 *            The gender
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * 
	 * @return The province
	 */
	public Object getProvince() {
		return province;
	}

	/**
	 * 
	 * @param province
	 *            The province
	 */
	public void setProvince(Object province) {
		this.province = province;
	}

	/**
	 * 
	 * @return The headImageUrl
	 */
	public String getHeadImageUrl() {
		return headImageUrl;
	}

	/**
	 * 
	 * @param headImageUrl
	 *            The head_image_url
	 */
	public void setHeadImageUrl(String headImageUrl) {
		this.headImageUrl = headImageUrl;
	}

	/**
	 * 
	 * @return The createdAt
	 */
	public String getCreatedAt() {
		return createdAt;
	}

	/**
	 * 
	 * @param createdAt
	 *            The created_at
	 */
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * 
	 * @return The birthday
	 */
	public String getBirthday() {
		return birthday;
	}

	/**
	 * 
	 * @param birthday
	 *            The birthday
	 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	
	public String toString() {
		return this.nickname;
	}
}