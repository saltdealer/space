package com.MyFunApp.NewFun.Util;
import android.content.Context;
import android.content.SharedPreferences;

public class share_preferences {

	/**
	 * @param args
	 * @return 
	 */
	public static String get_value(Context context, String section,  String value)
	{
		if(context == null)
		{
			return "";
		}
		return context.getSharedPreferences(section,0).getString(value,"");
	}
	public static void set_value(Context context, String section, String key, String value){
		if(context == null){
			return ;
		}
		SharedPreferences.Editor editor = context.getSharedPreferences(section,Context.MODE_PRIVATE).edit();
		editor.putString(key,value);
		editor.commit();
	}
}
