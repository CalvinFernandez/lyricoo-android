package com.lyricoo.ui;

import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.music.LyricooPlayer;
import com.lyricoo.music.Song;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * This view handles the display of a play button for mPlaying a Song.
 * 
 */
public class PlayButton extends RelativeLayout {
	// Default attributes
	private final boolean SHOW_SONG_TITLE_DEFAULT = false;
	private final int DEFAULT_SONG_TITLE_COLOR = R.color.black;
	private final int DEFAULT_SIZE_IN_DP = 40;
	private final int DEFAULT_PLAY_RESOURCE = R.drawable.ic_inbox_play;
	private final int DEFAULT_PAUSE_RESOURCE = R.drawable.ic_inbox_pause;

	// Default values for song title size and spacing. These should be scaled if
	// a custom size is set. Values are in DP.
	private final int TITLE_TOP_MARGIN = 15;
	private final int TITLE_MAX_WIDTH = 60;
	private final int TITLE_TEXT_SIZE = 12;

	/* store the set attributes */
	// whether or not to show the song title beneath the button
	private boolean mShowSongTitle;
	// if showing the song title, the color to make it
	private int mSongTitleColor;
	// the size of the play button icons. The icons should be square with each
	// side this length
	private int mSize;
	// the image resource id to show as a play icon
	private int mPlayResource;
	// the image resource id to show as a pause icon
	private int mPauseResource;

	// the ratio between the default size and the request size. Use this to
	// scale up song title sizing and spacing
	private float mScaleRatio;

	// The ImageView to show the button
	private ImageView mImage;

	// the ProgressBar to show for loading
	private ProgressBar mProgress;

	// the TextView to show the song title
	private TextView mSongTitle;

	// The song associated with this play button and the player to play it with
	private Song mSong;
	private LyricooPlayer mPlayer;
	private Context mContext;
	// whether or not we should be mPlaying. It is helpful to keep track of this
	// because if we are stopped while the song is loading it will try to play
	// after loading. With this we can do a check
	private boolean mPlaying;
	// keep track of whether we have mLoaded the song yet
	private boolean mLoaded;

	public PlayButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;

		// Get attributes
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.PlayButton, 0, 0);

		try {
			mShowSongTitle = a.getBoolean(R.styleable.PlayButton_showSongTitle,
					SHOW_SONG_TITLE_DEFAULT);
			mSongTitleColor = a.getColor(R.styleable.PlayButton_songTitleColor,
					getResources().getColor(DEFAULT_SONG_TITLE_COLOR));
			mSize = a.getDimensionPixelSize(R.styleable.PlayButton_size,
					dpToPixel(DEFAULT_SIZE_IN_DP));
			mPlayResource = a.getResourceId(R.styleable.PlayButton_playSrc,
					DEFAULT_PLAY_RESOURCE);
			mPauseResource = a.getResourceId(R.styleable.PlayButton_pauseSrc,
					DEFAULT_PAUSE_RESOURCE);
		} finally {
			// typedarray objects are a shared resource and must be recycled
			// after use
			a.recycle();
		}

		// determine the scale ratio to use in case a custom size was set
		mScaleRatio = ((float) mSize) / dpToPixel(DEFAULT_SIZE_IN_DP);

		// create the image view to hold the play/pause image
		mImage = new ImageView(context);

		// position image with layout parameters
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				mSize, mSize);

		// Center the icon
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		mImage.setLayoutParams(layoutParams);

		mImage.setImageResource(mPlayResource);
		
		// set an id so the song title can reference this
		mImage.setId(1);

		addView(mImage);

		// Create the progress bar
		mProgress = new ProgressBar(context);

		layoutParams = new RelativeLayout.LayoutParams(mSize, mSize);

		// Place progress in center
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		mProgress.setLayoutParams(layoutParams);

		// default to not shown
		mProgress.setVisibility(View.INVISIBLE);

		addView(mProgress);

		// add song title if requested
		if (mShowSongTitle) {
			mSongTitle = new TextView(context);

			layoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			// Put song title to the left, below the play image
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			layoutParams.addRule(RelativeLayout.BELOW, mImage.getId());
			layoutParams.setMargins(0,
					(int) (dpToPixel(TITLE_TOP_MARGIN) * mScaleRatio), 0, 0);

			mSongTitle.setLayoutParams(layoutParams);

			// Text should be on one line, ellipsized at the end if it doesn't
			// fit, and a max width should be specified so it doesn't run too
			// long. The text dimensions should be scaled
			mSongTitle.setEllipsize(TextUtils.TruncateAt.END);
			mSongTitle.setSingleLine();
			mSongTitle.setTextColor(mSongTitleColor);
			mSongTitle
					.setMaxWidth((int) (dpToPixel(TITLE_MAX_WIDTH) * mScaleRatio));
			mSongTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					dpToPixel(TITLE_TEXT_SIZE) * mScaleRatio);
			// default to not shown until a song is loaded
			mSongTitle.setVisibility(View.GONE);

			addView(mSongTitle);
		}
	}

	/**
	 * Set a song for mPlaying
	 * 
	 * @param song
	 */
	public void setSong(Song song) {
		mSong = song;
		mLoaded = false;

		if (song != null && mSongTitle != null) {
			mSongTitle.setText(song.getTitle());
			mSongTitle.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Clear any song that has been set
	 */
	public void clearSong() {
		if(mPlayer != null){
			mPlayer.stop();
		}
		
		mLoaded = false;
		mPlaying = false;
		mSong = null;

		if (mSongTitle != null) {
			mSongTitle.setText("");
			mSongTitle.setVisibility(View.GONE);
		}
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
	 * Start the song mPlaying and update the button view. A song must have
	 * first been mLoaded with setSong() otherwise nothing will happen
	 */
	public void play() {
		// if a song hasn't been set we can't play
		if (mSong == null) {
			return;
		}

		// flag to know that we should be playing
		mPlaying = true;

		// lazy initialization of player
		if (mPlayer == null) {
			mPlayer = new LyricooPlayer(mContext);
		}

		mProgress.setVisibility(View.VISIBLE);
		mImage.setVisibility(View.INVISIBLE);

		// if the song hasn't been mLoaded yet load it!
		if (!mLoaded) {
			mPlayer.loadSongFromUrl(mSong.getUrl(), new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					mLoaded = true;
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
		if (mPlaying) {
			mImage.setImageResource(mPauseResource);

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
	 * Stop the music mPlaying and update the button view
	 */
	public void stop() {
		mPlaying = false;

		if (mPlayer != null) {
			mPlayer.pause();
		}

		mProgress.setVisibility(View.INVISIBLE);
		mImage.setVisibility(View.VISIBLE);
		mImage.setImageResource(mPlayResource);
		setClickable(true);
	}

	/**
	 * Toggles the state of the play button. Stops it if it was mPlaying, or
	 * plays if it was stopped
	 */
	public void toggle() {
		if (mPlayer != null && mPlayer.isPlaying()) {
			stop();
		} else {
			play();
		}
	}

	private int dpToPixel(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	
	/**
	 * Release all music resources held by this player
	 */
	public void destroy(){
		clearSong();
		if(mPlayer != null){
			mPlayer.destroy();
		}		
	}
}
