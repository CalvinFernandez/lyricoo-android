package com.lyricoo;

import android.app.Application;

/*
 * The LyricooApp class provides a global storage area for
 * data that's shared between activities. We can store information
 * about the logged in user and their session.
 */

public class LyricooApp extends Application {
	// The currently logged in user. Null if the app is logged out	
	private User mUser = null;
	
	public Conversation conversationToDisplay = null;
	
	@Override
	  public void onCreate()
	  {
	    super.onCreate();
	    
	    // Initialize any singletons or variables here
	    
	  }

}
