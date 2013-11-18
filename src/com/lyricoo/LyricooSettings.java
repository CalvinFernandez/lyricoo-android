package com.lyricoo;

import android.content.Context;
import android.media.AudioManager;

/**
 * This singleton class holds the user's settings for the app. It must first be
 * initialized with initUserSettings which creates a default settings object.
 * Settings can then be retrieved by calling getUserSettings.
 * 
 * TODO: Sync user settings with server and allow settings to be initialized
 * with settings json
 * 
 * 
 */
public class LyricooSettings {
	private static LyricooSettings mSettingsSingleton;
	private Context mContext;

	// whether or not the user wants notifications sent to the system when they
	// get new messages
	private boolean mShowNotifications;
	// the volume songs are played at as set by audioManager.setStreamVolume
	private int mVolume;
	// the highest the volume can be set as determined by
	// audioManager.getStreamMaxVolume
	private int mMaxVolume;

	/**
	 * Create a settings object with defaults
	 * 
	 */
	private LyricooSettings(Context context) {
		mContext = context;
		
		// default to showing notifications
		mShowNotifications = true;

		// default to half the maximum volume
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		mVolume = mMaxVolume / 2;
	}

	/**
	 * Initialize a Settings singleton with default settings. If a singleton has
	 * already been intialized the existing one is returned.
	 * 
	 * @param context
	 * @return An newly initialized settings object with default settings, or
	 *         the existing Settings object if one was already initialized
	 */
	public static LyricooSettings initUserSettings(Context context) {
		if (mSettingsSingleton == null) {
			mSettingsSingleton = new LyricooSettings(context);
		}

		return mSettingsSingleton;
	}

	/**
	 * Retrieve the settings singleton. Must first be initialized with
	 * initUserSettings
	 * 
	 * @return The user settings or null if not yet initialized
	 */
	public static LyricooSettings getUserSettings() {
		return mSettingsSingleton;
	}

	public boolean showNotifications() {
		return mShowNotifications;
	}

	public void setShowNotifications(boolean showNotifications) {
		this.mShowNotifications = showNotifications;
	}

	public int getVolume() {
		return mVolume;
	}
	
	public int getMaxVolume(){
		return mMaxVolume;
	}

	public void setVolume(int volume) {
		this.mVolume = volume;
	}

}
