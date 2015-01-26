package com.MyFunApp.NewFun.Activity;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.MyFunApp.NewFun.BaseIActivity;
import com.MyFunApp.NewFun.R;
import com.MyFunApp.NewFun.Net.CONS;
import com.MyFunApp.NewFun.Util.share_preferences;
import com.MyFunApp.NewFun.View.NumericWheelAdapter;
import com.MyFunApp.NewFun.View.OnWheelChangedListener;
import com.MyFunApp.NewFun.View.WheelView;
import com.MyFunApp.NewFun.task.UpdateUserTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
//import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class RegInfoActivity extends BaseIActivity implements android.view.View.OnClickListener {

	private ImageView reg_photo_view;
	private View photo_Dailog=null;
	private View dataView = null;
	private ImageView photo_option[] = new ImageView[2];
	private TextView photo_cancel;
	private TextView reg_info_finish;
	private TextView reg_age_selector;
	private TextView reg_data_finish;
	private ViewStub reg_info_stub;
	private ViewStub reg_photo_stub;
	private TextView reg_nickname;
	private LinearLayout photo_cancel_layoutLayout; 
	private LinearLayout reg_info_photo_linear;
	private LinearLayout datapick_cancel_linear;
	private TextView chose_cookTextView;
	private TextView chose_tasteTextView;
	private TextView chose_locationTextView;
	private TextView show_cookTextView;
	private TextView show_tasteTextView;
	private TextView show_locationTextView;
	private TextView show_ageTextView;
	private RadioGroup reg_gender_radio;
	
	private String avatar_img_url ,local_image_path,account;

	private int ID[]= {R.id.dialog_camera,R.id.dialog_photo, R.id.photo_cancel};
	private static final String gender[]={"f","m"};
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
	private static final int PHOTO_REQUEST_GALLERY = 2;
	private static final int PHOTO_REQUEST_CUT = 3;
	private static final int PHOTO_NEXT = 4;
	private static final int INFO_NEXT = 5;
	private static int START_YEAR = 1964, END_YEAR = 2015;
	WheelView wv_year = null;
	WheelView wv_month = null;
	WheelView wv_day = null;
	Intent photo_data=null;
	ByteArrayOutputStream stream = null;
	File avatar_file = new File(Environment.getExternalStorageDirectory()+"/", getPhotoFileName());
	public RegInfoActivity() {
	}
	@Override
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reg_userinfo);		
		initViews();
		initEvents();
	}

	public void onClick(View paraView){
		switch (paraView.getId()) {
		case R.id.reg_info_avatar:
			click_avatar();
			break;
		case R.id.dialog_camera:
			click_camera();
			reg_info_finish.setVisibility(View.VISIBLE);
			break;
		case R.id.dialog_cancel_linear:
			dissmis_photo_Dailog();
			reg_info_finish.setVisibility(View.VISIBLE);
			break;
		case R.id.dialog_photo:
			click_chose_photo();
			reg_info_finish.setVisibility(View.VISIBLE);
			break;
		case R.id.photo_cancel:
			Log.i("click", "dialog_cancel");
		//	photo_Dailog.startAnimation(new MyScaler(1.0f, 1.0f, 1.0f, 0.5f, 500, photo_Dailog, true));
			dissmis_photo_Dailog();
			reg_info_finish.setVisibility(View.VISIBLE);
		//	photo_Dailog.dismiss();
			break;
		case R.id.datapick_cancel_linear:
			dataView.setVisibility(View.GONE);
			reg_info_finish.setVisibility(View.VISIBLE);
			break;
		case R.id.reg_info_cook_selector:
			click_cook();
			break;
		case R.id.reg_info_taste_selector:
			click_taste();
			break;
		case R.id.reg_info_location_selector:
			click_location();
			break;
		case R.id.reg_info_finish:
			click_commit();
			break;
		case R.id.reg_info_photo_linear:
			break;
		case R.id.reg_info_age_selector:
			click_age();
			break;
		case R.id.reg_data_finish:
			dataView.setVisibility(View.GONE);
			reg_info_finish.setVisibility(View.VISIBLE);
			Log.i("the data is ", wv_year.getCurrentItem()+" "+wv_day.getCurrentItem());
			
			String parten = "00";
			DecimalFormat decimal = new DecimalFormat(parten);
			show_ageTextView.setText((wv_year.getCurrentItem() + START_YEAR) + "-"
					 + decimal.format((wv_month.getCurrentItem() + 1)) + "-"
					 + decimal.format((wv_day.getCurrentItem() + 1)));
			break;
		default:
			break;
		}
	}
	
	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", size); 
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}
	
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		stream = new ByteArrayOutputStream();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");
			photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);//
			reg_photo_view.setImageBitmap(photo);
		}
		try {
			File jpgFile = new File(
					Environment.getExternalStorageDirectory(),
					java.lang.System.currentTimeMillis() + ".jpg");
			FileOutputStream outStream = new FileOutputStream(jpgFile);
			outStream.write( stream.toByteArray());
			outStream.close();
			local_image_path = jpgFile.getAbsolutePath();
			local_image_path = local_image_path.replace("/storage/emulated/", "/storage/sdcard");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			local_image_path = null;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case PHOTO_REQUEST_GALLERY:
				if (data != null) {
					startPhotoZoom(data.getData(), 150);
				}
				break;
			case PHOTO_REQUEST_TAKEPHOTO:
				if( avatar_file.exists())
				{
					startPhotoZoom(Uri.fromFile(avatar_file), 150);
					share_preferences.set_value(this,"user_info","photo_avatar",avatar_file.getPath());
				}
				break;
			case PHOTO_REQUEST_CUT:
				
				if (data != null)
					photo_data=data;
					setPicToView(data);
					dissmis_photo_Dailog();
					break;
				
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PHOTO_NEXT:
				int re = (Integer) msg.obj;
				if(re ==0){
					Toast.makeText(RegInfoActivity.this, "�� , photo uploaded", Toast.LENGTH_LONG).show();
				}
				break;
			case INFO_NEXT:
				int re1 = (Integer) msg.obj;
				if( re1 == 0){
					Toast.makeText(RegInfoActivity.this, "�� , ע��ɹ�", Toast.LENGTH_LONG).show();
				}
				RegInfoActivity.this.dissmis_photo_Dailog();
				break;
			default:
				break;
			}
		}
	};


	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		reg_photo_view = (ImageView) findViewById(R.id.reg_info_avatar);
		chose_cookTextView = (TextView) findViewById(R.id.reg_info_cook_selector);
		chose_tasteTextView = (TextView) findViewById(R.id.reg_info_taste_selector);
		reg_info_stub = (ViewStub) findViewById(R.id.reg_info_stub);
		reg_photo_stub = (ViewStub) findViewById(R.id.reg_info_timer_stub);
		chose_locationTextView = (TextView) findViewById(R.id.reg_info_location_selector);
		show_cookTextView = (TextView) findViewById(R.id.reg_info_label_cook);
		show_locationTextView = (TextView) findViewById(R.id.reg_info_label_location);
		show_tasteTextView = (TextView) findViewById(R.id.reg_info_label_taste);
		reg_info_finish = (TextView) findViewById(R.id.reg_info_finish);
		reg_nickname = (TextView) findViewById(R.id.reg_name_input);
		reg_gender_radio =(RadioGroup) findViewById(R.id.reg_info_radio_group);
		reg_age_selector = (TextView) findViewById(R.id.reg_info_age_selector);
		show_ageTextView = (TextView) findViewById(R.id.reg_info_label_age);
		this.account = share_preferences.get_value(this, "userinfo", "phone");
		
	}

	@Override
	protected void initEvents() {
		// TODO Auto-generated method stub
		reg_photo_view.setOnClickListener(this);
		chose_cookTextView.setOnClickListener(this);
		reg_info_finish.setOnClickListener(this);
		chose_tasteTextView.setOnClickListener(this);
		chose_locationTextView.setOnClickListener(this);
		reg_info_finish.setOnClickListener(this);
		reg_age_selector.setOnClickListener(this);
		
		
	}
	protected void click_age() {
		if(dataView == null){
			reg_info_stub.setLayoutResource(R.layout.dailog_data);
			dataView = reg_info_stub.inflate();
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DATE);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);

			// 添加大小月月份并将其转换为list,方便之后的判断
			String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
			String[] months_little = { "4", "6", "9", "11" };

			final List<String> list_big = Arrays.asList(months_big);
			final List<String> list_little = Arrays.asList(months_little);

			// 找到dialog的布局文件
			LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

			// 年
			wv_year = (WheelView) dataView.findViewById(R.id.year);
			wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
			wv_year.setCyclic(true);// 可循环滚动
			wv_year.setLabel("年");// 添加文字
			wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据
			// 月
			wv_month = (WheelView) dataView.findViewById(R.id.month);
			wv_month.setAdapter(new NumericWheelAdapter(1, 12));
			wv_month.setCyclic(true);
			wv_month.setLabel("月");
			wv_month.setCurrentItem(month);
			// 日
			wv_day = (WheelView) dataView.findViewById(R.id.day);
			wv_day.setCyclic(true);
			// 判断大小月及是否闰年,用来确定"日"的数据
			if (list_big.contains(String.valueOf(month + 1))) {
				wv_day.setAdapter(new NumericWheelAdapter(1, 31));
			} else if (list_little.contains(String.valueOf(month + 1))) {
				wv_day.setAdapter(new NumericWheelAdapter(1, 30));
			} else {
				// 闰年
				if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
					wv_day.setAdapter(new NumericWheelAdapter(1, 29));
				else
					wv_day.setAdapter(new NumericWheelAdapter(1, 28));
			}
			wv_day.setLabel("日");
			wv_day.setCurrentItem(day - 1);


			// 添加"年"监听
			OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					int year_num = newValue + START_YEAR;
					// 判断大小月及是否闰年,用来确定"日"的数据
					if (list_big
							.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 31));
					} else if (list_little.contains(String.valueOf(wv_month
							.getCurrentItem() + 1))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 30));
					} else {
						if ((year_num % 4 == 0 && year_num % 100 != 0)
								|| year_num % 400 == 0)
							wv_day.setAdapter(new NumericWheelAdapter(1, 29));
						else
							wv_day.setAdapter(new NumericWheelAdapter(1, 28));
					}
				}
			};
			// 添加"月"监听
			OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					int month_num = newValue + 1;
					// 判断大小月及是否闰年,用来确定"日"的数据
					if (list_big.contains(String.valueOf(month_num))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 31));
					} else if (list_little.contains(String.valueOf(month_num))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 30));
					} else {
						if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
								.getCurrentItem() + START_YEAR) % 100 != 0)
								|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
							wv_day.setAdapter(new NumericWheelAdapter(1, 29));
						else
							wv_day.setAdapter(new NumericWheelAdapter(1, 28));
					}
				}
			};
			wv_year.addChangingListener(wheelListener_year);
			wv_month.addChangingListener(wheelListener_month);

			// 根据屏幕密度来指定选择器字体的大小
			int textSize = 12;

			textSize = dip2px(this, textSize);

			wv_day.TEXT_SIZE = textSize;
			wv_month.TEXT_SIZE = textSize;
			wv_year.TEXT_SIZE = textSize;
			datapick_cancel_linear = (LinearLayout) dataView.findViewById(R.id.datapick_cancel_linear);
			datapick_cancel_linear.setOnClickListener(this);
			reg_data_finish = (TextView) dataView.findViewById(R.id.reg_data_finish);
			reg_data_finish.setOnClickListener(this);
			reg_info_finish.setVisibility(View.GONE);
		}else{
			dataView.setVisibility(View.VISIBLE);
			reg_info_finish.setVisibility(View.GONE);
		}
		
	}
	
	protected  void click_avatar() {
		Log.i("click", "avatar");
		//this.photo_Dailog = Dialog_fresh.show_photo_Dailog(this, this,photo_option,photo_cancel, ID);
		if(photo_Dailog == null){
			reg_photo_stub.setLayoutResource(R.layout.dailog_photo);
			photo_Dailog = reg_photo_stub.inflate();
			for (int i =0;i< photo_option.length;i++){
				photo_option[i] = (ImageView) photo_Dailog.findViewById(ID[i]);
				photo_option[i].setOnClickListener(this);
			}
			photo_cancel = (TextView) photo_Dailog.findViewById(R.id.photo_cancel);
			photo_cancel.setOnClickListener(this);
			photo_cancel_layoutLayout = (LinearLayout) photo_Dailog.findViewById(R.id.dialog_cancel_linear);
			photo_cancel_layoutLayout.setOnClickListener(this);
			reg_info_photo_linear = (LinearLayout) photo_Dailog.findViewById(R.id.reg_info_photo_linear);
			reg_info_photo_linear.setOnClickListener(this);
			reg_info_finish.setVisibility(View.GONE);
		}else{
			//photo_Dailog.startAnimation(new MyScaler(1.0f, 1.0f, 0.1f, 1.0f, 500, photo_Dailog, false));
			photo_Dailog.setVisibility(View.VISIBLE);
			reg_info_finish.setVisibility(View.GONE);
		}
	}
	protected void click_camera(){
		//photo_Dailog.dismiss();
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Log.i("the path", Uri.fromFile(avatar_file).toString());
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(avatar_file));
		startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
		Log.i("click","dialog_camera");
	}
	protected void dissmis_photo_Dailog(){
		photo_Dailog.setVisibility(View.GONE);
	}
	protected void click_chose_photo(){
		Log.i("click", "dialog_photo");
		//	photo_Dailog.dismiss();
			Intent intent2 = new Intent(Intent.ACTION_PICK, null);
			intent2.setDataAndType(
			MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(intent2, PHOTO_REQUEST_GALLERY);
	}

	protected void click_cook(){
		Log.i("click", "cook select");
		final String[] cookStrings = new String[] {"粤菜","川菜","云南菜"};
		//reg_info_stub.setLayoutResource(R.layout.dialog_interest);
		//View select_localView = reg_info_stub.inflate();
        OnClickListener listener_cook = new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				RegInfoActivity.this.show_cookTextView.setText(cookStrings[arg1]);
			}
		};
		new AlertDialog.Builder(this)  
		.setTitle("菜系")
		.setItems(cookStrings, listener_cook)    
		.show(); 
	}
	protected void click_taste(){
		Log.i("click", "cook select");
		final String[] tasteStrings = new String[] {"辣","酸","甜"};
		//reg_info_stub.setLayoutResource(R.layout.dialog_interest);
		//View select_localView = reg_info_stub.inflate();
        OnClickListener listener_taste = new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				RegInfoActivity.this.show_tasteTextView.setText(tasteStrings[arg1]);
			}
		};
		new AlertDialog.Builder(this)  
		.setTitle("口味")
		.setItems(tasteStrings, listener_taste)    
		.show(); 
	}
	protected void click_location()
	{
		Log.i("click", "cook select");
		final String[] locationStrings = new String[] {"杭州","上海","北京"};
		//reg_info_stub.setLayoutResource(R.layout.dialog_interest);
		//View select_localView = reg_info_stub.inflate();
        OnClickListener listener_location = new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				RegInfoActivity.this.show_locationTextView.setText(locationStrings[arg1]);
			}
		};
		new AlertDialog.Builder(this)  
		.setTitle("城市")
		.setItems(locationStrings, listener_location)    
		.show(); 
	}
	
	protected Boolean validate_data(){
		final String nicknameString = reg_nickname.getText().toString();
		final String genderString = "m";
		final String cookString = show_cookTextView.getText().toString();
		final String tasteString = show_tasteTextView.getText().toString();
		final String location = show_locationTextView.getText().toString();
		
		if( TextUtils.isEmpty(nicknameString) || TextUtils.isEmpty(genderString)||  TextUtils.isEmpty(cookString)|| TextUtils.isEmpty(tasteString)|| TextUtils.isEmpty(location) )
		{
			showCustomToast("信息不完整");
			return false;
		}
		if(photo_data == null)
		{
			showCustomToast("没有设置头像");
		}
		return true;
	}
	
	protected void click_commit(){
		final String nicknameString = reg_nickname.getText().toString();
		final String genderString;
		int radio_id = reg_gender_radio.getCheckedRadioButtonId();
		if(radio_id == R.id.reg_info_girl_radio){
			genderString="f";
		}else{
			genderString="m";
		}
		final String cookString = show_cookTextView.getText().toString();
		final String tasteString = show_tasteTextView.getText().toString();
		final String location = show_locationTextView.getText().toString();
		final String birthday = show_ageTextView.getText().toString();
		if(nicknameString.equals("")||cookString.equals("cookString") || local_image_path ==null || tasteString.equals("cookString") || location.equals("cookString")  ){
			showCustomToast("请填写完整");
			//return;
		}
		
		share_preferences.set_value(this, "user_info", "city", location);
		share_preferences.set_value(this, "user_info", "taste", tasteString);
		share_preferences.set_value(this, "user_info", "cook", cookString);
		UpdateUserTask updateusertask = new UpdateUserTask(RegInfoActivity.this,account,nicknameString, genderString,  local_image_path);
		putAsyncTask_sii(updateusertask);
/*
		putAsyncTask(new AsyncTask<Void, Void, Integer>() {
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showLoadingDialog("正在提交...");
			}
			@Override
			protected Integer doInBackground(Void... params) {
				int re;
				try {
					Log.i("invoke", "uploadimage");
					re = upload_image();
					if( re !=0){
						return -1;
					}
					re = update_usr_info(nicknameString, genderString, cookString, tasteString, location,birthday);
					if( re !=0){
						return -2;
					}
					return 0;
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return -1;
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				dismissLoadingDialog();
				if (result == 0) {
					showCustomToast("注册成功");
			//		Intent intent = new Intent(RegInfoActivity.this, LoginActivity.class);
		//			startActivity(intent);
		//			finish();
				} else {
					showCustomToast("注册失败");
				}
			}
		});
		*/
	}
	
	protected void click_finish(){
		final String nicknameString = reg_nickname.getText().toString();
		final String genderString = "m";
		final String cookString = show_cookTextView.getText().toString();
		final String tasteString = show_tasteTextView.getText().toString();
		final String location = show_locationTextView.getText().toString();

		Runnable  runnable_next = new Runnable() {
			@Override
			public void run() {
				int re;
			}
		};
		new Thread(runnable_next).start();
	}
	private int update_usr_info(String nickname,String gender, String cook, String taste ,String location,String birthday){
		
		HashMap<String, String> param = new HashMap<String, String>();
		
		String tokenString = share_preferences.get_value(this,"user_info","token");
		HashMap<String, String> reHashMap = null;
		
		CONS.token = tokenString;
		param.put("nickname", nickname);
		param.put("gender", gender);
		param.put("food_style", taste);
		param.put("food_category", cook);
		param.put("nickname", nickname);
		param.put("profile_image_url", this.avatar_img_url);
		param.put("birthday", birthday);
		Log.i("invoke", "update re   "+reHashMap.toString());
		
		if (reHashMap != null){
			return 0;
		}else{
			return -1;
		}
	}
	public void onBackPressed()
	{
	    if ((this.photo_Dailog != null) && (this.photo_Dailog.getVisibility() == View.VISIBLE))
	    {
	      this.photo_Dailog.setVisibility(View.GONE);
	      return;
	    }
	    if ((this.reg_info_stub != null) && (this.reg_info_stub.getVisibility() == 0))
	    {
	      this.reg_info_stub.setVisibility(View.GONE);
	      return;
	    }
	    super.onBackPressed();
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	public class MyScaler extends ScaleAnimation {

        private View mView;
        private LayoutParams mLayoutParams;

        private int mMarginBottomFromY, mMarginBottomToY;

        private boolean mVanishAfter = false;

        public MyScaler(float fromX, float toX, float fromY, float toY, int duration, View view,
                boolean vanishAfter) {
            super(fromX, toX, fromY, toY);
            setDuration(duration);
            mView = view;
            mVanishAfter = vanishAfter;
            mLayoutParams = (LayoutParams) view.getLayoutParams();
            int height = mView.getHeight();
            
            mMarginBottomFromY = (int) (height * fromY) + mLayoutParams.bottomMargin - height;
            mMarginBottomToY = (int) (0 - ((height * toY) + mLayoutParams.bottomMargin)) - height;
            Log.i("height is", ""+height+"from "+mMarginBottomFromY+"to "+mMarginBottomToY);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                int newMarginBottom = mMarginBottomFromY
                        + (int) ((mMarginBottomToY - mMarginBottomFromY) * interpolatedTime);
                mLayoutParams.setMargins(mLayoutParams.leftMargin, mLayoutParams.topMargin,
                    mLayoutParams.rightMargin, newMarginBottom);
                Log.i("scal", newMarginBottom+" "+mLayoutParams.topMargin);
                mView.getParent().requestLayout();
            } else {
            	if(mVanishAfter){
            	//	mView.setVisibility(View.GONE);
            	}else{
            		//mView.setVisibility(View.VISIBLE);
            	}
             //   mView.setVisibility(View.GONE);
            }
        }

    }

}
