package com.MyFunApp.NewFun.Activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.*;
import android.support.v7.appcompat.*;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.MyFunApp.NewFun.BaseActivity;
import com.MyFunApp.NewFun.adapter.indicatorAdapter;
import com.viewpagerindicator.UnderlinePageIndicator;
import com.MyFunApp.NewFun.R;

public class Guid_Activity extends BaseActivity implements  OnClickListener{

	ViewPager mPager;
	indicatorAdapter mAdapter;
	TextView loginTextView;
	TextView registertTextView;
	public Guid_Activity() {
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);   
		setContentView(R.layout.activity_guide_ui);
		
		mAdapter = new indicatorAdapter(getSupportFragmentManager());
		mPager =  (ViewPager) findViewById(R.id.guid_pager);
		mPager.setAdapter(mAdapter);
		UnderlinePageIndicator  indicator =  (UnderlinePageIndicator)findViewById(R.id.guid_indicator);
		indicator.setViewPager(mPager);
			
		initViews();
		initEvents();
		
	}
	

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		loginTextView = (TextView) findViewById(R.id.splash_login);
		registertTextView = (TextView) findViewById(R.id.splash_register);
		

	}

	@Override
	protected void initEvents() {
		// TODO Auto-generated method stub
		loginTextView.setOnClickListener(this);
		registertTextView.setOnClickListener(this);
	}

	@Override
	public void onClick(View paraView) {
		// TODO Auto-generated method stub
		switch (paraView.getId()) {
		case R.id.splash_login:
			Intent login_intentIntent = new Intent(this,Login_Activity.class);
			this.startActivity(login_intentIntent);
			break;
		case R.id.splash_register:
			Intent register_intentIntent = new Intent (this,RegPhoneActivity.class);
			this.startActivity(register_intentIntent);
			break;
		default:
			break;
		}
	}


	
		

}
