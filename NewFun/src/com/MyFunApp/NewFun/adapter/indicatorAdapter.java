package com.MyFunApp.NewFun.adapter;

import com.MyFunApp.NewFun.R;
import com.MyFunApp.NewFun.Activity.Fragment_Guide;
import com.MyFunApp.NewFun.db.Model;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class indicatorAdapter extends FragmentPagerAdapter {

	protected final int[] bg = {R.drawable.guid1_01, R.drawable.guid2_01,R.drawable.guid3_01, R.drawable.guid4_01,R.drawable.guid5_01,R.drawable.guid6_01};
	public indicatorAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return Fragment_Guide.new_instance( Model.splash_bg[arg0%Model.splash_bg.length]);
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.bg.length;
	}
}
