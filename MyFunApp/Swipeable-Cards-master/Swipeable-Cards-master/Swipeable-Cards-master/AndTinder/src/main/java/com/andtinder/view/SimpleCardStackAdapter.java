package com.andtinder.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.andtinder.R;
import com.andtinder.model.CardModel;

public final class SimpleCardStackAdapter extends CardStackAdapter {

	public SimpleCardStackAdapter(Context mContext) {
		super(mContext);
	}

	@Override
	public View getCardView(int position, CardModel model, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.std_card_inner, parent, false);
			assert convertView != null;
		}

		((ImageView) convertView.findViewById(R.id.image)).setImageDrawable(model.getCardImageDrawable());
		((RelativeLayout) convertView.findViewById(R.id.frame_bottom)).setAlpha(80);
		((TextView) convertView.findViewById(R.id.rank)).setText(model.getRank());
		((TextView) convertView.findViewById(R.id.time)).setText(model.getTime());
		((TextView) convertView.findViewById(R.id.name)).setText(model.getName());
		return convertView;
	}
}
