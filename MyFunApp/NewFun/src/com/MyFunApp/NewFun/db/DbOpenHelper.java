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
package com.MyFunApp.NewFun.db;


import de.greenrobot.daoMyFun.DaoMaster;
import de.greenrobot.daoMyFun.DaoMaster.DevOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbOpenHelper extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static DbOpenHelper instance;
	private static DevOpenHelper dianPing_instance=null;
	private static String user_data_name;
	private static String DIAN_PING_NAME = "bbdian.db";
	
	private static DevOpenHelper dbintance=null;

	private DbOpenHelper(Context context) {
		super(context, getUserDatabaseName(), null, DATABASE_VERSION);
	}
	public static DevOpenHelper getDianPingInstance(Context ctx) {
		if(dianPing_instance == null ){
			dianPing_instance = new DaoMaster.DevOpenHelper(ctx, DIAN_PING_NAME, null);
		}
		return dianPing_instance;
	}
	
	public static DevOpenHelper getdb_instance(Context ctx, String account){
		
		if( dbintance == null && !account.equals("null")){
			dbintance  = new DaoMaster.DevOpenHelper(ctx, account+".db", null);
			user_data_name = account+".db";
		}
		return dbintance;
	}
	public static DbOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DbOpenHelper(context.getApplicationContext());
		}
		return instance;
	}
	
	private static String getUserDatabaseName() {
		return user_data_name;
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	public void closeDB() {
	    if (instance != null) {
	        try {
	            SQLiteDatabase db = instance.getWritableDatabase();
	            db.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        instance = null;
	    }
	}
	
}
