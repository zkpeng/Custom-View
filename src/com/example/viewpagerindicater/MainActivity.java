package com.example.viewpagerindicater;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ViewPagerIndicater indicator;
	private ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_indicate);

		indicator = (ViewPagerIndicater) findViewById(R.id.indicate);
		List<String> titles = new ArrayList<String>();
		titles.add("Item1");
		titles.add("Item2");
		titles.add("Item3");
		indicator.setTitles(titles);
		pager = (ViewPager) findViewById(R.id.pager);
		List<View> list = new ArrayList<View>();
		for (int i = 0; i < titles.size(); i++) {
			View view = LayoutInflater.from(this).inflate(R.layout.tvitem, null);
			TextView tv = (TextView) view.findViewById(R.id.tt);
			switch (i) {
			case 1:
				tv.setBackgroundColor(Color.GRAY);
				break;
			default:
				break;
			}
			tv.setText("" + i);
			list.add(view);
		}
		pager.setAdapter(new MyPagerAdapter(list));
		indicator.setViewPager(pager);
	}

	public class MyPagerAdapter extends PagerAdapter {

		List<View> viewLists;

		public MyPagerAdapter(List<View> lists) {
			viewLists = lists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return viewLists.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View view, int position, Object object) {
			((ViewPager) view).removeView(viewLists.get(position));
		}

		@Override
		public Object instantiateItem(View view, int position) {
			((ViewPager) view).addView(viewLists.get(position), 0);
			return viewLists.get(position);
		}
	}
}
