package com.MyFunApp.NewFun.Util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class ClearEditText extends EditText {

	private Drawable clear_flag;

	void add_and_fresh(){
		fresh_clear_flag();	
	}
	
	private void fresh_clear_flag() {
		if (getText().toString().length() == 0)
		{
			setCompoundDrawables(null,null,null,null);
		}else{
			setCompoundDrawables(null,null,this.clear_flag,null);
		}
		
	}
	public ClearEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		add_and_fresh();
		addTextChangedListener(new ClearEditWatcher(this));
	}
	

	public ClearEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		addTextChangedListener(new ClearEditWatcher(this));
		// TODO Auto-generated constructor stub
		add_and_fresh();
	}

	public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		add_and_fresh();
		addTextChangedListener(new ClearEditWatcher(this));
	}
	

	 protected void finalize()
	 {
	   try {
		super.finalize();
	} catch (Throwable e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   this.clear_flag = null;
	 }

	public boolean onTouchEvent(MotionEvent paramMotionEvent)
	 {
		int action_up = MotionEvent.ACTION_UP;
		if(this.clear_flag != null && paramMotionEvent.getAction() == action_up){
		
			if( (paramMotionEvent.getX() <= getWidth() - getPaddingRight() - this.clear_flag.getIntrinsicWidth()) ||(paramMotionEvent.getX() >= getWidth() - getPaddingRight()))
			{
				
			}else {
				setText("");
			}
		}
		
		return super.onTouchEvent(paramMotionEvent);
	 }
	public void  setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
		if(right != null)
		{
			this.clear_flag = right;
		}
		super.setCompoundDrawables(left, top, right, bottom);
	}


}
