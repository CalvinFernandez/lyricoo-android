package com.lyricoo.music;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CategoryGridImage extends ImageView {
	public CategoryGridImage(Context context) {
		super(context);
	}

	public CategoryGridImage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CategoryGridImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// Snap to width
		setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
	}
}
