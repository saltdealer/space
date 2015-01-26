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
import com.MyFunApp.NewFun.Net.CONS;

import de.greenrobot.daoMyFun.DaoMaster;
import de.greenrobot.daoMyFun.DaoMaster.DevOpenHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.renderscript.Sampler.Value;
import android.util.Log;
import android.widget.Toast;


/**
 * 
 * ��¼�첽����.
 * 
 * @author shimiso
 */
public class SendCodeTask extends AsyncTask<String, Integer, Integer> {
	private final int SUCC = 100;
	private final int FAIL = 200;
	
	private IActivitySupport activitySupport;
	private String account;
	private String code_string;
	private HashMap<String, String> re_map;
	Handler handler;
	//private IUser user;
	
	public SendCodeTask(IActivitySupport activitySupport,String account,Handler handler) {
		this.activitySupport = activitySupport;
		this.account = account;
		this.handler = handler;
	}

	@Override
	protected void onPreExecute() {
		activitySupport.showLoadingDialog("正在提交...");
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(String... params) {
		re_map = CONS.get_verify_code(account);
		if(re_map == null){
			return FAIL;
		}
		int re_code =  Integer.parseInt(re_map.get("errcode"));
		
		if (re_code == 0)
		{
			return SUCC;
		}
		return FAIL;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	}

	@Override
	protected void onPostExecute(Integer result) {
		activitySupport.dismissLoadingDialog();
		switch (result) {
		case SUCC:
			activitySupport.showCustomToast(re_map.get("code"));
			handler.sendEmptyMessage(0);
			break;
		case FAIL:
			if(re_map == null){
				activitySupport.showCustomToast("网络错误");
			}else{
				activitySupport.showCustomToast(re_map.get("message"));
			}
			break;
		default:
			break;
		}
		super.onPostExecute(result);
	}
}
