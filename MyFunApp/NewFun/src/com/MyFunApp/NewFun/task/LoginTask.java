package com.MyFunApp.NewFun.task;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.MyFunApp.NewFun.IActivitySupport;
import com.MyFunApp.NewFun.Activity.Login_Activity;

import de.greenrobot.daoMyFun.DaoMaster;
import de.greenrobot.daoMyFun.DaoMaster.DevOpenHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


/**
 * 
 * ��¼�첽����.
 * 
 * @author shimiso
 */
public class LoginTask extends AsyncTask<String, Integer, Integer> {
	private final int SUCC = 100;
	private final int FAIL = 200;
	
	private ProgressDialog pd;
	private Context context;
	private IActivitySupport activitySupport;
	private String account,  passwd;
	
	//private IUser user;
	
	public LoginTask(IActivitySupport activitySupport,String account, String passwd) {
		this.passwd = passwd;
	}

	@Override
	protected void onPreExecute() {
		activitySupport.showLoadingDialog("正在登录");
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(String... params) {
		return login();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	}

	@Override
	protected void onPostExecute(Integer result) {
		//pd.dismiss();
		switch (result) {
		case SUCC:
			
			activitySupport.dismissLoadingDialog();
			break;
		case FAIL:
			//pd.dismiss();
			activitySupport.dismissLoadingDialog();
			activitySupport.showCustomToast("登录失败");
			break;
		default:
			break;
		}
		super.onPostExecute(result);
	}

	// ��¼
	private Integer login() {
	
		return SUCC;
	}
}
