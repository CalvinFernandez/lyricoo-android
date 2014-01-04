package com.lyricoo;

import java.util.ArrayList;

import org.apache.http.Header;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.lyricoo.music.Category;
import com.lyricoo.music.MusicManager;
import com.lyricoo.music.Song;
import com.lyricoo.session.Session;
import com.lyricoo.sync.LyricooPollingService;

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
		
		// do an initial sync of songs
		loadSongs();

	}

	private void loadSongs() {
		final Context context = this;
		MusicManager.getAll(new MusicManager.MusicHandler() {
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable e) {
				Utility.makeBasicToast(context, "Unable to load Lyricoos");
				
			}

			@Override
			public void onSuccess(ArrayList<Song> songs,
					ArrayList<Category> categories) {
				// TODO Auto-generated method stub
				
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
		if (!mIsGcmRegistered && Session.isLoggedIn()) {
			setPollingStatus(true);
		}
	}

	private void setPollingStatus(boolean doPolling) {
		Intent pollingIntent = new Intent(this, LyricooPollingService.class);
		pollingIntent.putExtra("doPolling", doPolling);
		this.startService(pollingIntent);
	}

}
