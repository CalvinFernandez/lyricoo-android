package com.lyricoo.ui;

import com.lyricoo.R;
import com.lyricoo.music.LyricooPlayer;
import com.lyricoo.music.Song;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * This view handles the display of a play button for playing a Song.
 * 
 */
public class PlayButton extends RelativeLayout {
	// Resource data for the play button
	// TODO: Allow custom icon size in xml attribute
	// Can only programmatically set pixels. Needs to convert from dp
	private final int ICON_SIZE_IN_DP = 40;
	private final int ICON_SIZE = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP, ICON_SIZE_IN_DP, getResources()
					.getDisplayMetrics());;
	private final int ICON_RESOURCE_PLAY = R.drawable.ic_inbox_play;
	private final int ICON_RESOURCE_PAUSE = R.drawable.ic_inbox_pause;

	// The ImageView to show the button
	private ImageView mImage;

	// the ProgressBar to show for loading
	private ProgressBar mProgress;

	// The song associated with this play button and the player to play it with
	private Song mSong;
	private LyricooPlayer mPlayer;
	private Context mContext;
	// whether or not we should be playing. It is helpful to keep track of this
	// because if we are stopped while the song is loading it will try to play
	// after loading. With this we can do a check
	private boolean playing;
	// keep track of whether we have loaded the song yet
	private boolean loaded;

	public PlayButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;

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
		mProgress.setVisibility(View.INVISIBLE);

		addView(mProgress);
	}

	/**
	 * Set a song for playing
	 * 
	 * @param song
	 */
	public void setSong(Song song) {
		mSong = song;
		loaded = false;
	}

	/**
	 * Get the song that was set
	 * 
	 * @return The last song set with setSong()
	 */
	public Song getSong() {
		return mSong;
	}

	/**
	 * Start the song playing and update the button view. A song must have first
	 * been loaded with setSong() otherwise nothing will happen
	 */
	public void play() {
		playing = true;
		// lazy initialization of player
		if (mPlayer == null) {
			mPlayer = new LyricooPlayer(mContext);
		}

		// if a song hasn't been set we can't play
		if (mSong == null) {
			return;
		}

		mProgress.setVisibility(View.VISIBLE);
		mImage.setVisibility(View.INVISIBLE);

		// if the song hasn't been loaded yet load it!
		if (!loaded) {
			mPlayer.loadSongFromUrl(mSong.getUrl(), new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					loaded = true;
					start();
				}
			});
		} else {
			start();
		}

	}

	/**
	 * Start or resume playback
	 */
	private void start() {
		if (playing) {
			mImage.setImageResource(ICON_RESOURCE_PAUSE);

			mProgress.setVisibility(View.INVISIBLE);
			mImage.setVisibility(View.VISIBLE);

			mPlayer.play(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// set button back to stopped state
					stop();
				}
			});
		}
	}

	/**
	 * Stop the music playing and update the button view
	 */
	public void stop() {
		playing = false;

		if (mPlayer != null) {
			mPlayer.pause();
		}

		mProgress.setVisibility(View.INVISIBLE);
		mImage.setVisibility(View.VISIBLE);
		mImage.setImageResource(ICON_RESOURCE_PLAY);
		setClickable(true);
	}

	/**
	 * Toggles the state of the play button. Stops it if it was playing, or
	 * plays if it was stopped
	 */
	public void toggle() {
		if (mPlayer.isPlaying()) {
			stop();
		} else {
			play();
		}
	}
}
