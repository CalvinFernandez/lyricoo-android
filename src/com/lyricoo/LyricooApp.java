package com.lyricoo;

import com.lyricoo.session.LyricooSettings;

import android.app.Application;

/*
 * The LyricooApp class provides a global storage area for
 * data that's shared between activities. We can store information
 * about the logged in user and their session.
 */

public class LyricooApp extends Application {	
	
	@Override
	  public void onCreate()
	  {
	    super.onCreate();
	    
	    // Initialize user settings
	    LyricooSettings.initUserSettings(getApplicationContext());	    
	    
	  }

}
