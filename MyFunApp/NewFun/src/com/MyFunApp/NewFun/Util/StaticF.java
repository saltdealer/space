package com.MyFunApp.NewFun.Util;

import com.MyFunApp.NewFun.BaseIActivity;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class StaticF {

	public StaticF() {
		// TODO Auto-generated constructor stub
	}

	static public void close_input_method(BaseIActivity activitysupport, Context ctx){
		InputMethodManager inputMethodManager;
		inputMethodManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (activitysupport.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (activitysupport.getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(activitysupport.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
