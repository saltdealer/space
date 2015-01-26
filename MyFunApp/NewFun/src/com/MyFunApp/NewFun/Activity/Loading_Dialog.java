package com.MyFunApp.NewFun.Activity;


import com.MyFunApp.NewFun.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Loading_Dialog extends Dialog {

	private ImageView localImageView;
	private TextView mHtvText;
	private String mText;

	public Loading_Dialog(Context context, String text) {
		super(context,R.style.loading_dialog);
		mText = text;
		init();
	}

	private void init() {
		setContentView(R.layout.dailog_loading);
		
		localImageView = (ImageView) findViewById(R.id.img_dialog);
		mHtvText = (TextView) findViewById(R.id.tipTextView);
		Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.loading_animation);
		localImageView.startAnimation(animation);
		
		mHtvText.setText(mText);
	}

	public void setText(String text) {
		mText = text;
		mHtvText.setText(mText);
	}

	@Override
	public void dismiss() {
		if (isShowing()) {
			super.dismiss();
		}
	}
}
