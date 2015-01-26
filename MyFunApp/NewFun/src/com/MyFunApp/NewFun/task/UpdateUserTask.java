package com.MyFunApp.NewFun.task;

import java.io.File;
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
public class UpdateUserTask extends AsyncTask<String, Integer, Integer> {
	private final int SUCC = 100;
	private final int UP_FAILE = 150;
	private final int FAIL = 101;
	
	private IActivitySupport activitySupport;
	private String account, nickname,female,image;
	private HashMap<String, String> re_map;
	private String token;
	private Intent intent_next;
	private Context context;

	//private IUser user;
	
	public UpdateUserTask(IActivitySupport activitySupport, String account, String nickname,String female,String image,Intent intent_next) {
		this.nickname = nickname;
		this.female = female;
		this.image = image;
		this.account = account;
		this.activitySupport = activitySupport;
		this.context = activitySupport.getContext();
		this.token = share_preferences.get_value(activitySupport.getContext(), "userinfo", "token");
		this.intent_next = intent_next;
		
	}

	@Override
	protected void onPreExecute() {
		activitySupport.showLoadingDialog("正在提交...");
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(String... params) {
		return do_registe();
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
			activitySupport.showCustomToast("注册成功");
			context.startActivity(intent_next);
			activitySupport.finish_activity();
			break;
		case UP_FAILE:
			activitySupport.dismissLoadingDialog();
			if(re_map == null){
				activitySupport.showCustomToast("网络错误，上传照片失败");
			}else{
				activitySupport.showCustomToast(re_map.get("message"));
			}
			break;
		case FAIL:
			//pd.dismiss();
			activitySupport.dismissLoadingDialog();
			if(re_map == null){
				activitySupport.showCustomToast("注册失败");
			}else{
				activitySupport.showCustomToast(re_map.get("message"));
			}
			break;
		default:
			break;
		}
		super.onPostExecute(result);
	}
	private Integer do_registe(){
		int errcode ;
		re_map = CONS.upload_profile_image(account,image, "jpg", token);
		errcode = Integer.parseInt(re_map.get("errcode"));
		if(re_map == null || errcode != 0){
			return UP_FAILE;
		}
		String image_num = re_map.get("id");
		re_map = CONS.update_user(nickname, account, female, image_num, token);
		errcode = Integer.parseInt(re_map.get("errcode"));
		if(re_map == null || errcode !=0 ){
			return FAIL;
		}else{
			return SUCC;
		}
	}
}
