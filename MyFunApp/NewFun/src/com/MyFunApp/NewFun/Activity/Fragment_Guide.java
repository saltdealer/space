package com.MyFunApp.NewFun.Activity;


import org.w3c.dom.Text;

import com.MyFunApp.NewFun.BaseApplication;
import com.MyFunApp.NewFun.R;
import com.MyFunApp.NewFun.Util.screenDensity;
import com.MyFunApp.NewFun.db.Model;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class Fragment_Guide extends Fragment {
	private static final String KEY_CONTENT ="guid_fragment:bg_index";

	int bg_index;
	public Fragment_Guide() {
		// TODO Auto-generated constructor stub
	}
	
	public static Fragment_Guide new_instance(int index){
		Fragment_Guide local_fragment = new Fragment_Guide();
		local_fragment.bg_index = index;
		return local_fragment;
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		if( (savedInstanceState != null) && (savedInstanceState.containsKey(KEY_CONTENT)) )
		{
			bg_index = savedInstanceState.getInt(KEY_CONTENT);
		}
			RelativeLayout relativeLayout = new RelativeLayout(getActivity());
			relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(-1,-1));
			relativeLayout.setBackgroundResource(this.bg_index);
			View localView = new View(getActivity());
			TextView localTextView = new TextView(getActivity());
			localTextView.setText(Model.splash_intro_Strings[bg_index%Model.splash_intro_Strings.length]);
			localTextView.setTextColor(getResources().getColor(R.color.white));
			//int param1 = screenDensity.get_bottom_delta(BaseApplication.application_context, 127.0F);
			int param2 = screenDensity.get_bottom_delta(BaseApplication.application_context, 40.0F);
			RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,param2);
			RelativeLayout.LayoutParams localLayoutParams2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,param2);
			localLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			localLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			localLayoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL);
			localLayoutParams2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		//	localLayoutParams.bottomMargin = screenDensity.get_bottom_delta(BaseApplication.application_context, 40.0F);
			localView.setLayoutParams(localLayoutParams);
		//	localView.setBackgroundResource(R.drawable.now_use_app);
			localTextView.setLayoutParams(localLayoutParams);
			localView.setBackgroundColor(getResources().getColor(R.color.black_73));
			localView.setAlpha(90);
			relativeLayout.addView(localView);
			localLayoutParams2.leftMargin=screenDensity.get_bottom_delta(BaseApplication.application_context, 20.0F);
			localLayoutParams2.rightMargin=screenDensity.get_bottom_delta(BaseApplication.application_context, 20.0F);
			localTextView.setLayoutParams(localLayoutParams2);
			relativeLayout.addView(localTextView);
			//localImageView.setOnClickListener(new now_use_listener(this));
			return relativeLayout;
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		outState.putInt(KEY_CONTENT, this.bg_index);
	}


	public class now_use_listener implements OnClickListener {

		final Fragment_Guide guide;
		public now_use_listener(Fragment_Guide guid_fragment) {
			// TODO Auto-generated constructor stub
			this.guide = guid_fragment;
		}

		@Override
		public void onClick(View imageView) {
			// TODO Auto-generated method stub
	
		//	Intent login_intentIntent = new Intent(guide.getActivity(),loginUI.class);
		//	this.guide.startActivity(login_intentIntent);
		}

}
}
