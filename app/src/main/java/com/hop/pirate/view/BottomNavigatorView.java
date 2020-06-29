package com.hop.pirate.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.hop.pirate.R;

import java.util.ArrayList;
import java.util.List;

public class BottomNavigatorView extends LinearLayoutCompat {


	private int mImageArray[] = { R.drawable.tab_home_normal ,R.drawable.tab_recharge_normal, R.drawable.tab_wallet_normal,
			R.drawable.tab_setting_normal };

	private int mImageSelectedArray[] = {R.drawable.tab_home_selected , R.drawable.tab_recharge_selected, R.drawable.tab_wallet_selected,
			R.drawable.tab_setting_selected };

	private int[] mTextArray = { R.string.tab_home,  R.string.tab_flow_market, R.string.tab_wallet, R.string.tab_setting };

	OnBottomNavigatorViewItemClickListener mOnBottomNavigatorViewItemClickListener;

	private View bottomNavigatorLine;

	public interface OnBottomNavigatorViewItemClickListener {

		void onBottomNavigatorViewItemClick(int position, View view);
	}

	public BottomNavigatorView(Context context) {
		this(context, null);
	}

	public BottomNavigatorView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	private List<FrameLayout> frameLayoutList = new ArrayList<>();

	public BottomNavigatorView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		setOrientation(HORIZONTAL);
		inflate(context, R.layout.layout_bottom_navigator, this);
		bottomNavigatorLine = findViewById(R.id.bottom_navigator_line);
		FrameLayout courseFl = findViewById(R.id.bottom_navigator_course);
		FrameLayout groupFl = findViewById(R.id.bottom_navigator_group_up);
		FrameLayout feedFl = findViewById(R.id.bottom_navigator_feed);
		FrameLayout mineFl = findViewById(R.id.bottom_navigator_mine);
		frameLayoutList.add(courseFl);
		frameLayoutList.add(groupFl);
		frameLayoutList.add(feedFl);
		frameLayoutList.add(mineFl);

		for (int i = 0; i < frameLayoutList.size(); i++) {
			final View view = frameLayoutList.get(i);
			final int finalI = i;
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (null != mOnBottomNavigatorViewItemClickListener) {
						mOnBottomNavigatorViewItemClickListener.onBottomNavigatorViewItemClick(finalI, view);
					}
				}
			});
		}

		initView();
	}

	private void initView() {
		for (int i = 0; i < frameLayoutList.size(); i++) {
			FrameLayout frameLayout = frameLayoutList.get(i);
			ImageView icon = frameLayout.findViewById(R.id.tab_title_iv);
			TextView text = frameLayout.findViewById(R.id.tab_title_tv);
			TextView badge = frameLayout.findViewById(R.id.tab_title_badge_tv);
			icon.setImageResource(mImageArray[i]);
			text.setText(mTextArray[i]);
		}
	}

	public void select(int position) {
		for (int i = 0; i < frameLayoutList.size(); i++) {
			View child = frameLayoutList.get(i);
			if (i == position) {
				selectChild(child, true, i);
			} else {
				selectChild(child, false, i);
			}
		}
	}

	private void selectChild(View child, boolean select, int position) {

		child.setSelected(select);
		ImageView icon = child.findViewById(R.id.tab_title_iv);
		TextView text = child.findViewById(R.id.tab_title_tv);
		TextView badge = child.findViewById(R.id.tab_title_badge_tv);
		if (select) {
			icon.setImageResource(mImageSelectedArray[position]);
			text.setTextColor(getResources().getColor(R.color.color_000000));
		} else {
			icon.setImageResource(mImageArray[position]);
			text.setTextColor(getResources().getColor(R.color.color_a4a4a7));
		}
	}

	public void setOnBottomNavigatorViewItemClickListener(OnBottomNavigatorViewItemClickListener listener) {
		this.mOnBottomNavigatorViewItemClickListener = listener;
	}

	public void showBadgeView(int position, int number, boolean show) {

		if (position < 0 || position >= frameLayoutList.size()) {
			return;
		}

		TextView badge = frameLayoutList.get(position).findViewById(R.id.tab_title_badge_tv);
		badge.setVisibility(show ? View.VISIBLE : View.GONE);
		if (number > 0) {
			badge.setText(number + "");
		} else {
			badge.setText("");
		}

	}
}