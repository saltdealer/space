package com.MyFunApp.NewFun.Activity;

import com.MyFunApp.NewFun.BaseActivity;
import com.MyFunApp.NewFun.R;
import com.MyFunApp.NewFun.Util.share_preferences;

import android.support.*;
import android.text.TextUtils;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;




public class Splash_Activity extends BaseActivity {
	long time;
	public void start_service()
	{
		
	}
	public void start_mainactivity(){
		new Thread(new mainActivity(this)).start();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.time = System.currentTimeMillis();
		requestWindowFeature(Window.FEATURE_NO_TITLE);   
		setContentView(R.layout.activity_slip__ui);
		start_mainactivity();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void initEvents() {
		// TODO Auto-generated method stub
		
	}
	public class mainActivity implements Runnable {
		Splash_Activity ui;
		public mainActivity(Splash_Activity ui) {
			// TODO Auto-generated constructor stub
			this.ui = ui;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			while( System.currentTimeMillis() - ui.time < 3000L){
				
			}
			if( TextUtils.equals("", share_preferences.get_value(this.ui,"userinfo","is_first"))  || 1==1)
			{
				Log.i(" i am here ","ok");
				share_preferences.set_value(this.ui,"userinfo","is_first","ok");
				Intent intent = new Intent(this.ui, Guid_Activity.class);
				this.ui.startActivity(intent);
			}else{
				//Intent login_intentIntent = new Intent(this.ui,LoginActivity.class);
				//this.ui.startActivity(login_intentIntent);
			}
		}

		/**
		 * @param args
		 */
	}
}
