package com.MyFunApp.NewFun;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

public interface IActivitySupport {
	/**
	 */
	public abstract BaseApplication getmApplication();

	/**
	 */
	public abstract void stopService();

	/**
	 * 
	 */
	public abstract void startService();

	/**
	 */
	public abstract boolean validateInternet();

	/**
	 */
	public abstract boolean hasInternetConnected();

	/**
	 */
	public abstract void isExit();

	/**
	 */
	public abstract boolean hasLocationGPS();

	/**
	 */
	public abstract boolean hasLocationNetWork();

	/**
	 */
	public abstract void checkMemoryCard();
	public abstract void showCustomToast(String text);

	/**
	 * 
	 */
	public abstract ProgressDialog getProgressDialog();

	public abstract Context getContext();

	public boolean getUserOnlineState();
	public void setUserOnlineState(boolean isOnline);

	public void setNotiType(int iconId, String contentTitle,
			String contentText, Class activity, String from);
	
	public void showLoadingDialog(String text) ;
	public void dismissLoadingDialog();
}
