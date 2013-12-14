package com.lyricoo.messages;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Extend ListView so we can override onSizeChange and listen for the view
 * changing height. This lets us detect when the keyboard pops up and changes
 * the list height, blocking the lower items.
 * 
 */
public class MessageList extends ListView {
	private onSizeChangedListener mSizeChangeListener;

	public MessageList(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		if (mSizeChangeListener != null) {
			mSizeChangeListener.onSizeChanged(w, h, oldw, oldh);
		}
	}

	public void setOnSizeChangedListener(onSizeChangedListener listener) {
		mSizeChangeListener = listener;
	}

	public interface onSizeChangedListener {
		public void onSizeChanged(int w, int h, int oldw, int oldh);
	}

}
