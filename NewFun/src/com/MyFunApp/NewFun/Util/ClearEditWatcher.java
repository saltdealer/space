package com.MyFunApp.NewFun.Util;

import android.text.Editable;
import android.text.TextWatcher;

public class ClearEditWatcher implements TextWatcher {

	ClearEditText editText;
	public ClearEditWatcher(ClearEditText edit) {
		// TODO Auto-generated constructor stub
		editText = edit;
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

		editText.add_and_fresh();
	}

}
