package com.MyFunApp.NewFun.task;

import java.io.UnsupportedEncodingException;
import java.net.ContentHandler;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.MyFunApp.NewFun.IActivitySupport;
import com.MyFunApp.NewFun.Activity.Login_Activity;
import com.MyFunApp.NewFun.Net.CONS;
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
public class CreateUserTask extends AsyncTask<String, Integer, Integer> {
	private final int SUCC = 100;
	private final int FAIL = 200;
	
	private ProgressDialog pd;
	private IActivitySupport activitySupport;
	private Context context;
	private String account,  passwd,code;
	private HashMap<String, String> re_map;
	private Intent intent_next;
	//private IUser user;
	
	public CreateUserTask(IActivitySupport activitySupport,String account, String passwd,String code,Intent intent_next ) {
		this.activitySupport = activitySupport;
		this.code = code;
		this.account = account;
		this.passwd = passwd;
		this.intent_next = intent_next;
		this.context = activitySupport.getContext();
	}

	@Override
	protected void onPreExecute() {
		activitySupport.showLoadingDialog("正在提交...");
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(String... params) {
		return create();
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
			activitySupport.showCustomToast("创建成功");
			String tokenstring = re_map.get("token");
			share_preferences.set_value(activitySupport.getContext(), "userinfo", "token", tokenstring);
			share_preferences.set_value(activitySupport.getContext(), "userinfo", "phone", account);
			context.startActivity(intent_next);
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
	private Integer create() {
		re_map = CONS.create_user(account, passwd, code);
		if(re_map == null)
		{
			return FAIL;
		}else{
			if(Integer.parseInt(re_map.get("errcode")) !=0 ){
				return FAIL;
			}
		}
		return SUCC;
	}
}
