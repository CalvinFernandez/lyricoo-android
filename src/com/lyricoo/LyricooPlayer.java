package com.lyricoo;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * Handles the playing of a song. Basically acts as a wrapper for the
 * MediaPlayer class to stream a song from a url and handle any
 * exceptions that get thrown
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

		// set volume to half max
		// TODO: Allow the user to adjust the volume somehow
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume / 2,
				0);
	}

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

		// set callback for when stream is ready to play
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
	 * @return True is playback starts successfully and false if there is an error
	 * 
	 */
	public boolean play(MediaPlayer.OnCompletionListener listener) {
		// register callback for when playback ends
		mPlayer.setOnCompletionListener(listener);

		try {
			mPlayer.start();
			return true;
		} catch (IllegalStateException e) {
			// reset the player if something went wrong
			reset();
			return false;
		}		
	}

	/**
	 * Stops playback if applicable
	 * 
	 */
	public void stop() {
		// only try to stop the music if something is playing
		if (mPlayer.isPlaying()) {
			try {
				mPlayer.stop();
			} catch (IllegalStateException e) {
				// reset the player if something went wrong
				reset();
			}
		}
	}
	
	/** 
	 * Reset the player and attempt to reload the last url if available
	 */
	private void reset(){
		// returns player to the uninitialized state
		mPlayer.reset();
		
		if(mUrl != null){
			loadSongFromUrl(mUrl, null);
		}
	}
	
	/**
	 * Checks whether music is playing
	 * 
	 * @return True is music is currently playing, false otherwise
	 */
	public boolean isPlaying(){
		return mPlayer.isPlaying();
	}

}
