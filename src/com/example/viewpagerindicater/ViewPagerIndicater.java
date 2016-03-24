package com.example.viewpagerindicater;
/** 
 * @Description: ViewPager的指示器  
 * @author zkp(1015717288@qq.com)   
 * @date 2016-03-24  
 * @version V1.0    
 */
import java.util.ArrayList;
import java.util.List;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewPagerIndicater extends ViewGroup implements
		OnPageChangeListener, OnClickListener {

	private Context mContext;
	private ViewPager mViewPager;
	public static int COLOR_INDICATOR = Color.parseColor("#45C01A");
	public static int COLOR_TEXT_UNSELECTED = Color.parseColor("#000000");
	public static int COLOR_TEXT_SELECTED = COLOR_INDICATOR;
	public static int WIDTH_INDICATOR = 13;
	public static float ANIMATION_DURATION = 5000.0f;
	private LinearLayout mTabContainer;
	private int mCurSel = 0;
	private int mCurPos = 0;
	private Paint mIndicatePaint;
	private List<String> mTitles;
	private List<Point> mPoints;
	private float iIndicaterStart = 0;
	private List<ValueAnimator> mAnimations;

	public ViewPagerIndicater(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO 自动生成的构造函数存根
		mContext = context;
		mIndicatePaint = new Paint();
		mIndicatePaint.setColor(COLOR_INDICATOR);
		mIndicatePaint.setAntiAlias(true);
		mIndicatePaint.setStyle(Style.STROKE);
		mIndicatePaint.setStrokeWidth(WIDTH_INDICATOR);

		mTitles = new ArrayList<String>();
		mAnimations = new ArrayList<ValueAnimator>();
	}

	@Override
	protected void onFinishInflate() {
		// TODO 自动生成的方法存根
		mTabContainer = new LinearLayout(mContext);
		mTabContainer.setOrientation(LinearLayout.HORIZONTAL);
		mTabContainer.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(mTabContainer);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO 自动生成\的方法存根
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				measureChild(child, widthMeasureSpec, heightMeasureSpec);
			}
		}
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO 自动生成的方法存根
		View child = getChildAt(0);
		child.layout(l, t, r, b);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO 自动生成的方法存根
		TextView child = (TextView) mTabContainer.getChildAt(mCurSel);
		int top = child.getBottom();
		int index1 = mCurPos;
		int index2 = Math.min(mTabContainer.getChildCount() - 1, mCurPos + 1);

		TextView tv1 = (TextView) mTabContainer.getChildAt(index1);
		TextView tv2 = (TextView) mTabContainer.getChildAt(index2);
		if (mCurSel == mCurPos)// 从左向右划
		{
			tv2.setTextColor((int) Float.parseFloat(mAnimations.get(2)
					.getAnimatedValue().toString()));
			tv1.setTextColor((int) Float.parseFloat(mAnimations.get(1)
					.getAnimatedValue().toString()));
		}
		if (Math.abs(mCurSel - mCurPos) == 1)// 从右向左划
		{
			tv1.setTextColor((int) Float.parseFloat(mAnimations.get(1)
					.getAnimatedValue().toString()));
			tv2.setTextColor((int) Float.parseFloat(mAnimations.get(2)
					.getAnimatedValue().toString()));
		}
		if (Math.abs(mCurSel - mCurPos) > 1)// 处理点击选择
		{
			TextView tvSelected = (TextView) mTabContainer.getChildAt(mCurSel);
			TextView tvPos = (TextView) mTabContainer.getChildAt(mCurPos);
			tvSelected.setTextColor(COLOR_TEXT_SELECTED);
			tvPos.setTextColor(COLOR_TEXT_UNSELECTED);

			int min = mCurSel < mCurPos ? mCurSel : mCurPos;
			int max = mCurSel > mCurPos ? mCurSel : mCurPos;
			for (int i = min + 1; i < max; i++) {
				((TextView) mTabContainer.getChildAt(mCurSel))
						.setTextColor(COLOR_TEXT_UNSELECTED);
			}
		}
		canvas.drawLine(
				// why cann't use "iIndicaterStart"??? confused...
				Float.parseFloat(mAnimations.get(0).getAnimatedValue()
						.toString()),
				top,
				Float.parseFloat(mAnimations.get(0).getAnimatedValue()
						.toString())
						+ child.getWidth(), top, mIndicatePaint);
	}

	public void setViewPager(ViewPager viewpager) {
		this.mViewPager = viewpager;
		mViewPager.setOnPageChangeListener(this);
		mTabContainer.removeAllViews();
		for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
			TextView tab = new TextView(mContext);
			tab.setText(mTitles.get(i));
			tab.setGravity(Gravity.CENTER);
			tab.setClickable(true);
			tab.setTag(i);
			tab.setTextSize(18);
			tab.setOnClickListener(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
					LayoutParams.MATCH_PARENT, 1.0f);
			mTabContainer.addView(tab, i, params);
		}
		invalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO 自动生成的方法存根
		super.onSizeChanged(w, h, oldw, oldh);
		initPositions();
	}

	private void initPositions() {
		// TODO 自动生成的方法存根
		mPoints = new ArrayList<Point>();
		for (int j = 0; j < mTabContainer.getChildCount(); j++) {
			View child = mTabContainer.getChildAt(j);
			mPoints.add(new Point(child.getMeasuredWidth() * j, child
					.getBottom()));
		}
	}

	@SuppressLint("NewApi")
	private void initAnimations(int pos) {
		mAnimations.clear();
		float fromX = mPoints.get(pos).x;
		int i = Math.min(mTabContainer.getChildCount() - 1, pos + 1);
		float toX = mPoints.get(i).x;

		ObjectAnimator leftPointAnimator = ObjectAnimator.ofFloat(
				ViewPagerIndicater.this, "iIndicaterStart", fromX, toX);
		leftPointAnimator.setDuration((long) ANIMATION_DURATION);
		leftPointAnimator.setInterpolator(new AccelerateInterpolator());
		mAnimations.add(leftPointAnimator);

		ValueAnimator leftPaintColorAnimator = ObjectAnimator.ofInt(
				COLOR_TEXT_SELECTED, COLOR_TEXT_UNSELECTED);
		leftPaintColorAnimator.setDuration((long) ANIMATION_DURATION);
		leftPaintColorAnimator.setEvaluator(new ArgbEvaluator());
		leftPaintColorAnimator
				.setInterpolator(new AccelerateDecelerateInterpolator());
		mAnimations.add(leftPaintColorAnimator);

		ValueAnimator rightPaintColorAnimator = ObjectAnimator.ofInt(
				COLOR_TEXT_UNSELECTED, COLOR_TEXT_SELECTED);
		rightPaintColorAnimator.setDuration((long) ANIMATION_DURATION);
		rightPaintColorAnimator.setEvaluator(new ArgbEvaluator());
		rightPaintColorAnimator
				.setInterpolator(new AccelerateDecelerateInterpolator());
		mAnimations.add(rightPaintColorAnimator);
	}

	public void setTitles(List<String> titles) {
		this.mTitles = titles;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onPageScrolled(int position, float offset, int arg2) {
		// TODO 自动生成的方法存根
		mCurPos = position;
		initAnimations(position);
		for (ValueAnimator animator : mAnimations) {
			animator.setCurrentPlayTime((long) (ANIMATION_DURATION * offset));
		}
		// Also works...
		// iIndicaterStart = mTabContainer.getChildAt(0).getWidth() * position +
		// mTabContainer.getChildAt(0).getWidth() * offset;
		invalidate();
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO 自动生成的方法存根
		mCurSel = arg0;
	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		int tag = (Integer) v.getTag();
		// mCurSel = tag;
		// Also works...
		// iIndicaterStart = mTabContainer.getChildAt(0).getWidth() * mCurSel;
		mViewPager.setCurrentItem(tag);
		invalidate();
	}

	public float getiIndicaterStart() {
		return iIndicaterStart;
	}

	public void setiIndicaterStart(float iIndicaterStart) {
		this.iIndicaterStart = iIndicaterStart;
	}

}
