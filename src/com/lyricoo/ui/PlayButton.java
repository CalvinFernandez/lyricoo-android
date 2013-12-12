package com.lyricoo.ui;

import com.lyricoo.R;
import com.lyricoo.music.Song;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class PlayButton extends RelativeLayout {
	// Resource data for the play button
	// TODO: Allow custom icon size in xml attribute
	private final int ICON_SIZE = 60;
	private final int ICON_RESOURCE_PLAY = R.drawable.ic_inbox_play;
	private final int ICON_RESOURCE_PAUSE = R.drawable.ic_inbox_pause;

	// The ImageView to show the button
	private ImageView mImage;

	// the ProgressBar to show for loading
	private ProgressBar mProgress;
	
	// The song associated with this play button
	private Song mSong;

	public PlayButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		// create the image view to hold the play/pause image
		mImage = new ImageView(context);

		// position image with layout parameters
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				ICON_SIZE, ICON_SIZE);

		// center
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		mImage.setLayoutParams(layoutParams);

		// Default to show the play button
		mImage.setImageResource(ICON_RESOURCE_PLAY);

		addView(mImage);

		// Create the progress bar
		mProgress = new ProgressBar(context);

		layoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		// center
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		// default to not shown
		mProgress.setVisibility(View.GONE);

		addView(mProgress);
	}

	public void reset() {
		mProgress.setVisibility(View.GONE);
		mImage.setVisibility(View.VISIBLE);
		mImage.setImageResource(ICON_RESOURCE_PLAY);
	}

	public void setLoading() {
		mImage.setVisibility(View.GONE);
		mProgress.setVisibility(View.VISIBLE);

	}

	public void setPlaying() {
		mProgress.setVisibility(View.GONE);
		mImage.setVisibility(View.VISIBLE);
		mImage.setImageResource(ICON_RESOURCE_PAUSE);
	}

	public void setStopped() {
		mProgress.setVisibility(View.GONE);
		mImage.setVisibility(View.VISIBLE);
		mImage.setImageResource(ICON_RESOURCE_PLAY);
	}
	
	public void setSong(Song song){
		mSong = song;
	}
	
	public Song getSong(){
		return mSong;
	}

}
