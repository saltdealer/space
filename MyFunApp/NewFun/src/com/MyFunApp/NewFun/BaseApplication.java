package com.MyFunApp.NewFun;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.MyFunApp.NewFun.Net.DianApiTool;
import com.MyFunApp.NewFun.Util.ThreadPoolUtils;
import com.MyFunApp.NewFun.Util.share_preferences;
import com.MyFunApp.NewFun.db.DbOpenHelper;
import com.MyFunApp.NewFun.db.Model;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import de.greenrobot.daoMyFun.DaoMaster;
import de.greenrobot.daoMyFun.DaoMaster.DevOpenHelper;
import de.greenrobot.daoMyFun.DaoSession;
import de.greenrobot.daoMyFun.DianPingData;
import de.greenrobot.daoMyFun.DianPingDataDao;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class BaseApplication extends Application {
	public static Context application_context;
	private static BaseApplication instance;

	private List<Activity> activityList = new LinkedList<Activity>(); //activiy container
	public LocationClient mLocationClient = null;
	public LocationListener myListener;
	public double mLongitude=0;
	public double mLatitude=0;
	public double mRadius =0;

	public BaseApplication() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate() {
		super.onCreate();
		this.application_context = this;
		this.instance = this;
		mLocationClient = new LocationClient(getApplicationContext());     // init baidu location sdk
		myListener = new LocationListener();
		mLocationClient.registerLocationListener( myListener );     // register location listener
		InitLocation();	// init parameter of baidu sdk
		System.out.println("start to invoke baidu location sdk in application");
		mLocationClient.start();
		ThreadPoolUtils.execute(new load_data());
	}

	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 遍历所有Activity并finish
	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
	}
	
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Device_Sensors);//���ö�λģʽ
		option.setCoorType("gcj02");//���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
		int span=1000;
		try {
			span = Integer.valueOf("1000");
		} catch (Exception e) {
			// TODO: handle exception
		}
		option.setScanSpan(span);//���÷���λ����ļ��ʱ��Ϊ5000ms
		option.setIsNeedAddress(false);
		mLocationClient.setLocOption(option);
	}
	
	public class LocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			Log.i("location", "null");
			if (location == null){
				share_preferences.set_value(getApplicationContext(), "user_info", "location", "fail");
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			} 
			Log.i("loocation",sb.toString());
			mLocationClient.stop();
			if(location.getLocType()< 162){
				share_preferences.set_value(getApplicationContext(), "user_info", "latitude", String.valueOf(location.getLatitude()));
				share_preferences.set_value(getApplicationContext(), "user_info", "lontitude", String.valueOf(location.getLongitude()));
				share_preferences.set_value(getApplicationContext(), "user_info", "radius", String.valueOf(location.getRadius()));
				share_preferences.set_value(getApplicationContext(), "user_info", "location", "success");

				mLongitude = location.getLongitude();
				mLatitude = location.getLatitude();
				mRadius = location.getRadius();
			}
		}
	}
	
	public class load_data implements Runnable {

		//private Handler hand;
		private String url;
		private SQLiteDatabase db;
		private DaoMaster daoMaster;
	    private DaoSession daoSession;
	    private DianPingDataDao dianpingdao;
		public load_data() {
			DevOpenHelper helper = DbOpenHelper.getDianPingInstance(BaseApplication.this);
			db = helper.getWritableDatabase();
			daoMaster = new DaoMaster(db);
		    daoSession = daoMaster.newSession();
		    dianpingdao = daoSession.getDianPingDataDao();
		}
		@Override
		public void run() {
			
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
			int update_need = 0;
			Log.i("in dian thread","yes ");
			Calendar calendar = Calendar.getInstance();
		    Date date_now= calendar.getTime();
			if( TextUtils.equals("", share_preferences.get_value(getApplicationContext(),"userinfo","dian_ready")) )
			{
				
				update_need = 1;
				DianPingData City_data = new DianPingData(null, "cities", Model.online_citiesStrings, new Date(0));
				dianpingdao.insert(City_data);
		        Log.i("插入的dianping data", City_data.toString());
		        
		        DianPingData category_data = new DianPingData(null, "categories", Model.online_categoriesStrings, new Date(0));
		        dianpingdao.insert(category_data);
		        Log.i("插入的category ",category_data.toString());
				share_preferences.set_value(getApplicationContext(),"userinfo","dian_ready","ok");
			}
			String requestResult = "";
			List<DianPingData> dian_datas  = dianpingdao.loadAll();
			for(DianPingData data: dian_datas){
				Date date = data.getUpdate_time();
				long delta =  (date_now.getTime() - date.getTime())/1000/60/60/24/7;
				
				if(  (date_now.getTime() - date.getTime())/1000/60/60/24/7 > 1  ){
					if( data.getType().equals("cities")){
						Map<String, String> paramMap = new HashMap<String, String>();
						requestResult = DianApiTool.requestApi(Model.url_dian_cities_onlineString, Model.dian_key, Model.dian_value, paramMap);
						JSONObject cities_object;
						try {
							cities_object = new JSONObject(requestResult);
							if( cities_object.getString("status").equals("OK"))
							{
								JSONArray cities_Array = cities_object.getJSONArray("cities");
								String citiesString = cities_Array.toString();
								data.setUpdate_time(new Date(System.currentTimeMillis()));
								data.setData(citiesString);
						        dianpingdao.update(data);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}else if(data.getType().equals("categories")){
						Map<String, String> paramMap = new HashMap<String, String>();
						requestResult = DianApiTool.requestApi(Model.url_dian_categories, Model.dian_key, Model.dian_value, paramMap);
						JSONObject categories_object;
						try {
							categories_object = new JSONObject(requestResult);
							if( categories_object.getString("status").equals("OK"))
							{
								JSONArray categories_Array = categories_object.getJSONArray("categories");
								String categoriesString = categories_Array.toString();
								data.setUpdate_time(new Date(System.currentTimeMillis()));
								data.setData(categoriesString);
								dianpingdao.update(data);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
			}
		}
	};
}
