package com.MyFunApp.NewFun.Util;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class screenDensity {

	private static float shift1;
	private static float shift2;
	private static float shift3;
	
	public screenDensity() {
		// TODO Auto-generated constructor stub
		this.shift1 = 15.0F;
		this.shift2 = 15.0F;
		this.shift3 = 15;
	}
	
	public static int  get_bottom_delta(Context context, float shift){
		
		
		return (int)(0.5F + shift * context.getResources().getDisplayMetrics().density);
	}

	public static Bitmap rotate_bitmap90(Context context,Bitmap bitmap){
		
		
		   Matrix m = new Matrix();
           m.setRotate(90, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
       
       		Class<?> c = null;
			Object obj = null;
			Field field = null;
			int x = 0, statusBarHeight = 0;
			try {
				c = Class.forName("com.android.internal.R$dimen");
				obj = c.newInstance();
				field = c.getField("status_bar_height");
				x = Integer.parseInt(field.get(obj).toString());
				statusBarHeight = context.getResources().getDimensionPixelSize(x);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		Bitmap new_bitmap  = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), m, true);
		bitmap.recycle();
		return new_bitmap;
	}
	

}
