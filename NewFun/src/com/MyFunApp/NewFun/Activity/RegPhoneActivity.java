package com.MyFunApp.NewFun.Activity;


import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import org.apache.http.TokenIterator;
import org.json.JSONException;
import org.json.JSONObject;

import com.MyFunApp.NewFun.BaseActivity;
import com.MyFunApp.NewFun.BaseIActivity;
import com.MyFunApp.NewFun.R;
import com.MyFunApp.NewFun.Net.CONS;
import com.MyFunApp.NewFun.Util.share_preferences;
import com.MyFunApp.NewFun.task.CreateUserTask;
import com.MyFunApp.NewFun.task.SendCodeTask;

import android.R.integer;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class RegPhoneActivity extends BaseIActivity implements OnClickListener{

	private EditText reg_phone_number;   
	private EditText reg_passwd;
	private EditText verify_code;
	private TextView reg_next;
	private TextView reg_send_code;
	private TextView btn_send_verify;
	private TextView countryCode;
	private Dialog loading_diaDialog;
	private ImageView app_back;
	String phoneString;
	String passwdString;
	String verifyString;
	private CheckBox agreed_clause;
	private final static int VERIFY = 1;
	private final static int NEXT = 2;
	
	private int mReSendTime = 60;
	
	public RegPhoneActivity() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reg_phone);		
		
		initViews();
		initEvents();
	}
	
	public void onClick(View paraView){
		switch (paraView.getId()) {
		case R.id.login_login_btn:
			break;
		case R.id.reg_next:
			reg_next();
			break;
		case R.id.btn_send_verify:
			reg_send();
			break;
		case R.id.agreed_clause:
			if(agreed_clause.isChecked()){
			}else{
			}
			break;
		case R.id.app_back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		reg_next = ((TextView) findViewById(R.id.reg_next)) ;
		btn_send_verify = ((TextView) findViewById(R.id.btn_send_verify));
		reg_phone_number = ((EditText) findViewById(R.id.reg_phone_number)) ;
		this.verify_code = ((EditText ) findViewById( R.id.reg_verify_number));
		this.reg_passwd = ((EditText) findViewById(R.id.reg_pass_word));
		this.agreed_clause = (CheckBox)findViewById(R.id.agreed_clause);
		app_back = (ImageView) findViewById(R.id.app_back);
		
	}

	@Override
	protected void initEvents() {
		// TODO Auto-generated method stub
		this.reg_next.setOnClickListener(this);
		this.btn_send_verify.setOnClickListener(this);
		this.agreed_clause.setOnClickListener(this);
		this.app_back.setOnClickListener(this);
	}
	
	private boolean validatePwd() {
		passwdString = null;
		String pwd = reg_passwd.getText().toString().trim();
		if (pwd.length() < 4) {
			showCustomToast("密码长度小于4");
			reg_passwd.requestFocus();
			return false;
		}
		if (pwd.length() > 16) {
			showCustomToast("密码长度大于16");
			reg_passwd.requestFocus();
			return false;
		}
		passwdString = pwd;
		return true;
	}
	private boolean isNull(EditText editText) {
		String text = editText.getText().toString().trim();
		if (text != null && text.length() > 0) {
			return false;
		}
		return true;
	}
	
	private boolean matchPhone(String text) {
		if (Pattern.compile("(\\d{11})|(\\+\\d{3,})").matcher(text).matches()) {
			return true;
		}
		return false;
	}
	
	private boolean validateAccount() {
		phoneString = null;
		if (isNull(reg_phone_number)) {
			showCustomToast("电话号码为空");
			reg_phone_number.requestFocus();
			return false;
		}
		String account = reg_phone_number.getText().toString().trim();
		if (matchPhone(account)) {
			if (account.length() < 3) {
				showCustomToast("帐户不符合规则");
				reg_phone_number.requestFocus();
				return false;
			}
		}
		phoneString = this.reg_phone_number.getText().toString().trim();
		return true;
	}

	private void reg_next(){
		passwdString = this.reg_passwd.getText().toString().trim();
		phoneString = this.reg_phone_number.getText().toString().trim();
		verifyString = this.verify_code.getText().toString().trim();
		if( !agreed_clause.isChecked()){
			showCustomToast("请先同意条款");
			return;
		}
		if ((!validateAccount()) || (!validatePwd()) ) {
			return;
		}
		Intent intent_next = new Intent(RegPhoneActivity.this, RegInfoActivity.class);
		CreateUserTask createusertask = new CreateUserTask(RegPhoneActivity.this, phoneString, passwdString, verifyString, intent_next);
		putAsyncTask_sii(createusertask);
	}
	private void reg_send(){
	//	dismissLoadingDialog();
		this.phoneString = this.reg_phone_number.getText().toString().trim();
		this.passwdString = this.reg_passwd.getText().toString().trim();
		this.verifyString = this.verify_code.getText().toString().trim();
		Log.i("the string is     ", phoneString);
		if ((!validateAccount()) ) {
			return;
		}
		SendCodeTask sendcodetask = new SendCodeTask(RegPhoneActivity.this,phoneString,handler);
		putAsyncTask_sii(sendcodetask);
	}
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mReSendTime > 1) {
				mReSendTime--;
				btn_send_verify.setEnabled(false);
				btn_send_verify.setText("重发(" + mReSendTime + ")");
				handler.sendEmptyMessageDelayed(0, 1000);
			} else {
				mReSendTime = 60;
				btn_send_verify.setEnabled(true);
				btn_send_verify.setText("重    发");
			}
		}
	};
}
