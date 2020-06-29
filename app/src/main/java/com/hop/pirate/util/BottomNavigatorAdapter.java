package com.hop.pirate.util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

public class BottomNavigatorAdapter implements FragmentNavigatorAdapter {

	public static class TabInfo {

		final @NonNull
		String tag;
		final @NonNull
		Class<?> clss;
		final @Nullable
		Bundle args;

		public TabInfo(@NonNull String _tag, @NonNull Class<?> _class, @Nullable Bundle _args) {
			tag = _tag;
			clss = _class;
			args = _args;
		}
	}

	private ArrayList<TabInfo> tabInfoArrayList = new ArrayList<>();

	private Context mContext;

	public BottomNavigatorAdapter(Context context) {
		this.mContext = context;
	}

	@Override
	public Fragment onCreateFragment(int position) {

		TabInfo tabInfo = tabInfoArrayList.get(position);
		return Fragment.instantiate(mContext, tabInfo.clss.getName(), tabInfo.args);

	}

	@Override
	public String getTag(int position) {
		return tabInfoArrayList.get(position).tag;
	}

	@Override
	public int getCount() {
		return tabInfoArrayList.size();
	}

	public void addTab(TabInfo tabInfo) {
		tabInfoArrayList.add(tabInfo);
	}
}
