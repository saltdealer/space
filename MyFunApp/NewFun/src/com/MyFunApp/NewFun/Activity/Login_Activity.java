package com.MyFunApp.NewFun.Activity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.MyFunApp.NewFun.BaseIActivity;
import com.MyFunApp.NewFun.R;
import com.MyFunApp.NewFun.task.LoginTask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class Login_Activity extends BaseIActivity implements OnClickListener {

	private EditText login_account_et;
	private EditText login_pwd_et;
	private TextView login_login_btn;
	private TextView login_register_btn;
	private TextView login_login_forget;
	private ProgressDialog pd;
	String accountString;
	String passwdString;
	private ImageView app_back;
	protected SharedPreferences preferences;
	public Login_Activity() {
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);	
		pd = getProgressDialog();
		initViews();
		initEvents();
		init();
	}
	@Override
	protected void onResume() {
		super.onResume();
		checkMemoryCard();
		validateInternet();
	}
	
	public void onClick(View paraView){
		switch (paraView.getId()) {
		case R.id.login_login_btn:
			click_login();
			break;
		case R.id.login_register_btn:
		//	Intent login_intentIntent = new Intent (this,RegPhoneActivity.class);
		//	this.startActivity(login_intentIntent);
			break;
		case R.id.app_back:
			finish();
			break;
		default:
			break;
		}
		Log.i("xiaojian", ""+paraView.getId());
	}
	
	protected void init(){
	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		this.login_account_et = ((EditText) findViewById(R.id.login_account_et));
		this.login_pwd_et     = ((EditText) findViewById(R.id.login_pwd_et));
		this.login_login_btn  = ((TextView) findViewById(R.id.login_login_btn));
		this.login_register_btn = ((TextView) findViewById(R.id.login_register_btn));
		this.login_login_forget = ((TextView) findViewById(R.id.login_login_forget));
		this.app_back = (ImageView) findViewById(R.id.app_back);
	}

	@Override
	protected void initEvents() {
		// TODO Auto-generated method stub
		this.login_login_btn.setOnClickListener(this);	
		this.login_register_btn.setOnClickListener(this);
		this.login_login_forget.setOnClickListener(this);
		this.app_back.setOnClickListener(this);
	}
	
	
    Boolean validate(){
    	accountString = this.login_account_et.getText().toString().trim();
	    passwdString = this.login_pwd_et.getText().toString().trim();
		if ( (!TextUtils.isEmpty(accountString)) && (!TextUtils.isEmpty(passwdString))){
			
			return true;
		}
		showCustomToast("请填写完整信息");
		return false;
    }

	protected void click_login(){
		if( !validate() && validateInternet()){
			
			return ;
		}
	Intent intent_next = new Intent(Login_Activity.this, MainActivity.class);
	LoginTask loginTask = new LoginTask(Login_Activity.this,
				accountString,passwdString,intent_next);
	putAsyncTask_sii(loginTask);
	}
}
