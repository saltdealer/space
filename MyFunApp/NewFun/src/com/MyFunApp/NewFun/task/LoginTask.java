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
import com.MyFunApp.NewFun.Net.IUser;
import com.MyFunApp.NewFun.Util.StaticF;
import com.MyFunApp.NewFun.Util.share_preferences;

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
	private Intent intent_next;
	HashMap<String, String> re_map = null;
	//private IUser user;
	
	public LoginTask(IActivitySupport activitySupport,String account, String passwd, Intent intent_next) {
		this.passwd = passwd;
		this.account = account;
		this.activitySupport = activitySupport;
		this.context = activitySupport.getContext();
		this.intent_next = intent_next;
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
			activitySupport.showCustomToast("登录成功");
			String tokenstring = re_map.get("token");
			share_preferences.set_value(activitySupport.getContext(), "userinfo", "token", tokenstring);
			share_preferences.set_value(activitySupport.getContext(), "userinfo", "phone", account);
			context.startActivity(intent_next);
			activitySupport.finish_activity();
			break;
		case FAIL:
			activitySupport.dismissLoadingDialog();
			if(re_map == null){
				activitySupport.showCustomToast("登录失败");
			}else{
				activitySupport.showCustomToast(re_map.get("message"));
			}
			break;
		default:
			break;
		}
		super.onPostExecute(result);
	}

	// ��¼
	private Integer login() {
		String md5passwdString = StaticF.stringToMD5(passwd);
		re_map = CONS.login(account,md5passwdString);
		int errcode = Integer.parseInt(re_map.get("errcode"));
		if(re_map == null || errcode != 0){
			return FAIL;
		}
		return SUCC;
	}
}
