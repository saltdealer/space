package com.MyFunApp.NewFun.Net;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IUser {

	@SerializedName("phone")
	@Expose
	private String phoneNum;
	
	@SerializedName("image")
	@Expose
	private String profileImageUrl;
	
	@SerializedName("name")
	@Expose
	private String nickname;
	
	@SerializedName("female")
	@Expose
	private int gender;
	
	@SerializedName("token")
	@Expose
	private String token;
	

	public String getToken(){
		return token;
	}
	
	public void setToken(String token){
		this.token = token;
	}
	
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
	public int getGender() {
		return gender;
	}

	/**
	 * 
	 * @param gender
	 *            The gender
	 */
	public void setGender(int gender) {
		this.gender = gender;
	}
	
	public String toString() {
		return this.nickname;
	}
}