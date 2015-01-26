/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.MyFunApp.NewFun.Activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.MyFunApp.NewFun.BaseApplication;
import com.MyFunApp.NewFun.BaseIActivity;
import com.MyFunApp.NewFun.R;
import com.MyFunApp.NewFun.db.DbOpenHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.daoMyFun.Conversation_msgDao;
import de.greenrobot.daoMyFun.Conversation_tribe;
import de.greenrobot.daoMyFun.Conversation_tribeDao;
import de.greenrobot.daoMyFun.DaoMaster;
import de.greenrobot.daoMyFun.DaoSession;
import de.greenrobot.daoMyFun.DaoMaster.DevOpenHelper;


public class MainActivity extends BaseIActivity {

	protected static final String TAG = "MainActivity";
	// 未读消息textview
	private TextView unreadLabel;
	// 未读通讯录textview
	private TextView unreadAddressLable;
	private TextView[] mTabs_text;
    
	private ImageView[] mTabs;
	private InputMethodManager inputMethodManager;
	private Fragment[] fragments;
	private int index;
	private RelativeLayout[] tab_containers;
	private int currentTabIndex;
	public boolean isConflict = false;
	
	private Conversation_msgDao conversation_msgDao;
	
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
    private DaoSession daoSession;
    private Conversation_tribeDao conversation_tribeDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            finish();
            //startActivity(new Intent(this, LoginActivity.class));
            return;
        }
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
		setContentView(R.layout.activity_main);
		initViews();
		
		//MobclickAgent.setDebugMode( true );
		//MobclickAgent.updateOnlineConfig(this);
		
		if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow){
			
		}
		
		// 这个fragment只显示好友和群组的聊天记录
		// chatHistoryFragment = new ChatHistoryFragment();
		// 显示所有人消息记录的fragment
		/*
		chatHistoryFragment = new ChatAllHistoryFragment();
		contactListFragment = new MyContactlistFragment();
		settingFragment = new SettingsFragment();
		MainFragment = new MainFragment();
		fragments = new Fragment[] {contactListFragment , chatHistoryFragment,MainFragment, settingFragment };
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, chatHistoryFragment)
				.add(R.id.fragment_container, contactListFragment).hide(chatHistoryFragment).show(contactListFragment).commit();
				*/

	}

	/**
	 * 初始化组件
	 */
	@Override
	protected void initViews() {
		//
//		unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
	//	unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
		mTabs = new ImageView[4];
		mTabs_text = new TextView[4];
		mTabs[0] = (ImageView) findViewById(R.id.btn_contact_list);
		mTabs[1] = (ImageView) findViewById(R.id.btn_invite);
		mTabs[2] = (ImageView) findViewById(R.id.btn_discover_list);
		mTabs[3] = (ImageView) findViewById(R.id.btn_setting);
		mTabs_text[0] = (TextView) findViewById(R.id.btn_text_contact);
		mTabs_text[1] = (TextView) findViewById(R.id.btn_text_invite);
		mTabs_text[2] = (TextView) findViewById(R.id.btn_text_discover);
		mTabs_text[3] = (TextView) findViewById(R.id.btn_text_setting);
		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);
		mTabs_text[0].setSelected(true);

	}

	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.btn_contact_list:
			index = 0;
			break;
		case R.id.btn_invite:
			index = 1;
			break;
		case R.id.btn_discover_list:
			index = 2;
			break;
		case R.id.btn_setting:
			index = 3;
			break;
		}
	/*	if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		*/
		mTabs[currentTabIndex].setSelected(false);
		mTabs_text[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		mTabs_text[index].setSelected(true);
		currentTabIndex = index;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注销广播接收者
		try {
		//	unregisterReceiver(msgReceiver);
		} catch (Exception e) {
		}
		try {
	//		unregisterReceiver(ackMessageReceiver);
		} catch (Exception e) {
		}
		// try {
		// unregisterReceiver(offlineMessageReceiver);
		// } catch (Exception e) {
		// }

		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}

	}

	
	@Override
	protected void onResume() {
		super.onResume();
		if (!isConflict) {
		}

	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isConflict", isConflict);
        super.onSaveInstanceState(outState);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private android.app.AlertDialog.Builder conflictBuilder;
	private boolean isConflictDialogShow;


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow){
			
		}
	}

	

	@Override
	protected void initEvents() {
		// TODO Auto-generated method stub
		
	}
	
}
