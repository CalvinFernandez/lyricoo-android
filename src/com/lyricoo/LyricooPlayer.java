package com.lyricoo;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;

/**
 * Handles the playing of a song. Basically acts as a wrapper for the
 * MediaPlayer class to stream a song from a url and handle any exceptions that
 * get thrown
 * 
 * 
 */
public class LyricooPlayer {
	// TODO: Can use mediaplayer.release() to free resources used by the song
	// TODO: Deal with AudioFocus

	private String mUrl;
	private MediaPlayer mPlayer;
	private Context mContext;

	/**
	 * Initialize a player without loading any songs
	 */
	public LyricooPlayer(Context context) {
		mContext = context;
		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		// set volume according to the user's settings
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		int volume = LyricooSettings.getUserSettings().getVolume();
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
	}

	/**
	 * Prepares a song to stream from http
	 * 
	 * @param url
	 *            The url to stream from
	 * @param listener
	 *            Listener to call when the song is ready to play. If null,
	 *            playback starts automatically when ready
	 */
	public void loadSongFromUrl(String url,
			MediaPlayer.OnPreparedListener listener) {
		mUrl = url;
		// TODO: Handle caught exceptions
		// reset player to initialized state
		mPlayer.reset();

		// attempt to load music from url
		try {
			mPlayer.setDataSource(mUrl);
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (IllegalStateException e) {
		} catch (IOException e) {
		}

		// set callback for when stream is ready to play. If none is provided,
		// create one that plays when ready
		if (listener == null) {
			listener = new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.start();
				}
			};
		}

		mPlayer.setOnPreparedListener(listener);

		try {
			// might take long! (for buffering, etc)
			mPlayer.prepareAsync();
		} catch (IllegalStateException e) {
		}
	}

	/**
	 * Plays the song from the beginning
	 * 
	 * @return True is playback starts successfully and false if there is an
	 *         error
	 * 
	 */
	public boolean play(MediaPlayer.OnCompletionListener listener) {
		// register callback for when playback ends
		mPlayer.setOnCompletionListener(listener);

		try {
			mPlayer.start();
		} catch (Exception e) {
			// reset the player if something went wrong
			reset();
			return false;
		}

		return true;
	}

	/**
	 * Stops playback and prepares the player to start playback again
	 * 
	 */
	public void stop() {
		if (mPlayer.isPlaying()) {
			try {
				mPlayer.stop();
				mPlayer.setOnPreparedListener(null);
				mPlayer.prepareAsync();
			} catch (Exception e) {
				// reset the player if something went wrong
				reset();
			}
		}

	}

	/**
	 * Pauses playback at the current spot
	 * 
	 */
	public void pause() {
		if (mPlayer.isPlaying()) {
			try {
				mPlayer.pause();
			} catch (Exception e) {
				// reset the player if something went wrong
				reset();
			}
		}
	}

	/**
	 * Reset the player and attempt to reload the last url if available
	 */
	private void reset() {
		// returns player to the uninitialized state
		mPlayer.reset();

		if (mUrl != null) {
			loadSongFromUrl(mUrl, null);
		}
	}

	/**
	 * Checks whether music is playing
	 * 
	 * @return True is music is currently playing, false otherwise
	 */
	public boolean isPlaying() {
		return mPlayer.isPlaying();
	}

	/**
	 * Stop music and free all player resources
	 * 
	 */
	public void destroy() {
		mPlayer.release();
	}

}
