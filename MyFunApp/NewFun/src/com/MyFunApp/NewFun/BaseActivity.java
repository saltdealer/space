package com.MyFunApp.NewFun;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.MyFunApp.NewFun.Activity.Loading_Dialog;
import com.MyFunApp.NewFun.Util.*;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;


public abstract class BaseActivity extends FragmentActivity  {

	protected BaseApplication mApplication;
	protected Loading_Dialog mLoadingDialog;
	protected Context context = null;
	
	protected SharedPreferences preferences;
	
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected float mDensity;
	protected ProgressDialog pg = null;
	
	private static final int notifiId = 11;
    protected NotificationManager notificationManager;
	
	protected List<AsyncTask<Void, Void, Integer>> mAsyncTasks = new ArrayList<AsyncTask<Void, Void, Integer>>();
	protected List<AsyncTask<String, Integer, Integer>> mAsyncTasks_sii = new ArrayList<AsyncTask<String, Integer, Integer>>();
	public BaseActivity() {
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);   
		mApplication = (BaseApplication) getApplication();
		mLoadingDialog = new Loading_Dialog(this, "正在请求");
		context= this;

//		preferences = getSharedPreferences(Constant.LOGIN_SET, 0);
		pg = new ProgressDialog(context);
		
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		mDensity = metric.density;
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}


	@Override
	protected void onDestroy() {
		clearAsyncTask();
		super.onDestroy();
	}
	protected abstract void initViews();

	protected abstract void initEvents();

	public BaseApplication getmApplication(){
		return this.mApplication;
	}

	protected void putAsyncTask(AsyncTask<Void, Void, Integer> asyncTask) {
		mAsyncTasks.add(asyncTask.execute());
	}
	protected void putAsyncTask_sii(AsyncTask<String, Integer, Integer> asyncTask) {
		mAsyncTasks_sii.add(asyncTask.execute());
	}
	
	protected void clearAsyncTask() {
		Iterator<AsyncTask<Void, Void, Integer>> iterator = mAsyncTasks
				.iterator();
		while (iterator.hasNext()) {
			AsyncTask<Void, Void, Integer> asyncTask = iterator.next();
			if (asyncTask != null && !asyncTask.isCancelled()) {
				asyncTask.cancel(true);
			}
		}
		mAsyncTasks.clear();
		Iterator<AsyncTask<String, Integer, Integer>> iterator_sii = mAsyncTasks_sii
				.iterator();
		while (iterator_sii.hasNext()) {
			AsyncTask<String, Integer, Integer> asyncTask = iterator_sii.next();
			if (asyncTask != null && !asyncTask.isCancelled()) {
				asyncTask.cancel(true);
			}
		}
		mAsyncTasks_sii.clear();
		
	}
	
	public void showLoadingDialog(String text) {
		if (mLoadingDialog.isShowing()) {
			return;
		}
		if (text != null) {
			mLoadingDialog.setText(text);
		}
		mLoadingDialog.show();
	}
	
	public void dismissLoadingDialog() {
		if (mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}

	
	protected void showShortToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
	}

	protected void showShortToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	protected void showLongToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show();
	}
	
	protected void showLongToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	protected void showCustomToast(int resId) {
		View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(
				R.layout.toast_common, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text))
				.setText(getString(resId));
		Toast toast = new Toast(BaseActivity.this);
		toast.setGravity(Gravity.BOTTOM, 20, 20);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	public void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(
				R.layout.toast_common, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(BaseActivity.this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	protected void showLogDebug(String tag, String msg) {
		Log.d(tag, msg);
	}

	protected void showLogError(String tag, String msg) {
		Log.e(tag, msg);
	}

	protected void startActivity(Class<?> cls) {
		startActivity(cls, null);
	}

	protected void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}
	protected void startActivity(String action) {
		startActivity(action, null);
	}

	protected void startActivity(String action, Bundle bundle) {
		Intent intent = new Intent();
		intent.setAction(action);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	protected AlertDialog showAlertDialog(String title, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message).show();
		return alertDialog;
	}
	
	protected AlertDialog showAlertDialog(String title, String message,
			String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener)
				.show();
		return alertDialog;
	}

	protected AlertDialog showAlertDialog(String title, String message,
			int icon, String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message).setIcon(icon)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener)
				.show();
		return alertDialog;
	}

	protected void defaultFinish() {
		super.finish();
	}
	
	public void openWirelessSet() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder
				.setTitle(R.string.prompt)
				.setMessage(context.getString(R.string.check_connection))
				.setPositiveButton(R.string.menu_settings,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								Intent intent = new Intent(
										Settings.ACTION_WIRELESS_SETTINGS);
								context.startActivity(intent);
							}
						})
				.setNegativeButton(R.string.close,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						});
		dialogBuilder.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
	  //  MobclickAgent.onResume(this);
	}

	@Override
    protected void onStart() {
        super.onStart();
      //  MobclickAgent.onPause(this);
    }	
	
    /**
     * 返回
     * 
     * @param view
     */
    public void back(View view) {
        finish();
    }
    public void finish_activity(){
    	finish();
    }

}
