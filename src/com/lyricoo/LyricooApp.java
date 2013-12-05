package com.lyricoo;

import java.util.ArrayList;

import org.apache.http.Header;

import com.lyricoo.messages.LyricooPollingService;
import com.lyricoo.music.MusicManager;
import com.lyricoo.music.Song;
import com.lyricoo.session.LyricooSettings;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

/*
 * The LyricooApp class provides a global storage area for
 * data that's shared between activities. We can store information
 * about the logged in user and their session.
 */

public class LyricooApp extends Application {
	private boolean mIsGcmRegistered;

	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize user settings
		LyricooSettings.initUserSettings(getApplicationContext());
		
		// do an initial sync of songs
		loadSongs();

	}

	private void loadSongs() {
		final Context context = this;
		MusicManager.getAll(new MusicManager.MusicHandler() {
			
			@Override
			public void onSuccess(ArrayList<Song> songs, ArrayList<String> categories) {
				// Don't need to do anything
				
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable e) {
				Utility.makeBasicToast(context, "Unable to load Lyricoos");
				
			}
		});
	}

	/**
	 * Set whether or not the app successfully registered an id with gcm
	 * 
	 * @param registered
	 */
	public void setIsGcmRegistered(boolean registered) {
		mIsGcmRegistered = registered;
	}

	/**
	 * Get whether or not we are registered with gcm
	 * 
	 * @return
	 */
	public boolean getIsGcmRegistered() {
		return mIsGcmRegistered;
	}

	/**
	 * Called when a LyricooActivity pauses
	 * 
	 */
	public void pause() {
		if (!mIsGcmRegistered) {
			setPollingStatus(false);
		}
	}

	/**
	 * Called when a LyricooActivity resumes
	 * 
	 */
	public void resume() {
		if (!mIsGcmRegistered) {
			setPollingStatus(true);
		}
	}

	private void setPollingStatus(boolean doPolling) {
		Intent pollingIntent = new Intent(this, LyricooPollingService.class);
		pollingIntent.putExtra("doPolling", doPolling);
		this.startService(pollingIntent);
	}

}
