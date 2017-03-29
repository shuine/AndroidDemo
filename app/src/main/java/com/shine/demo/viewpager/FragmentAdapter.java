package com.shine.demo.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class FragmentAdapter extends FragmentPagerAdapter {
	protected static final String[] CONTENT = new String[] { "This", "Is Is", "A A A", "Test", };

	public FragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return TestFragment.newInstance(CONTENT[position]);
	}

	@Override
	public int getCount() {
		return CONTENT.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return CONTENT[position];
	}
}