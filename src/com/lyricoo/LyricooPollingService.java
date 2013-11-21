package com.lyricoo;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;

public class LyricooPollingService extends IntentService {
	private static Handler mHandler;
	private static Runnable mPoller;
	private static boolean mIsPolling;

	// how often we should poll, in milliseconds
	private static final int POLLING_INTERVAL = 10 * 1000;

	public LyricooPollingService() {
		super("Lyricoo Polling");
		
		// initialize the first time this is called
		if(mHandler == null){
			mIsPolling = false;

			mHandler = new Handler();

			mPoller = new Runnable() {
				@Override
				public void run() {
					if(mIsPolling){
						poll();
						
						// schedule another poll later
						mHandler.postDelayed(mPoller, POLLING_INTERVAL);
					}				
				}
			};
		}		
	}

	@Override
	protected void onHandleIntent(Intent workIntent) {
		boolean doPolling = workIntent.getBooleanExtra("doPolling", false);
		Utility.log("Intent received. Do polling? " + doPolling);

		if (doPolling) {
			startPolling();
		} else {
			stopPolling();
		}

	}

	private void startPolling() {
		if(!mIsPolling){
			mIsPolling =  true;
			Utility.log("Starting polling");
			mHandler.post(mPoller);
		}		
	}

	private void stopPolling() {
		mIsPolling = false;
		Utility.log("Stopping polling");
		
		// if we pass null, all callbacks are removed
		mHandler.removeCallbacksAndMessages(null);
	}

	/**
	 * Poll the server for updates
	 */
	private void poll() {
		Utility.log("Poll called");
		// Just tell the conversation manager to do a sync
		// TODO: More sophisticated/efficient polling
		ConversationManager conversationManager = Session
				.getConversationManager();
		if (conversationManager != null) {
			conversationManager.sync(null);
		}
	}

}
