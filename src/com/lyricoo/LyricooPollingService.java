package com.lyricoo;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;

/**
 * This service handles polling to the server when gcm isn't working for
 * notifications. To enable or disable polling, start LyricooPollingService with
 * a "doPolling" extra set to true or false
 * 
 * Example:
 * 
 * Intent pollingIntent = new Intent(this, LyricooPollingService.class);
 * pollingIntent.putExtra("doPolling", doPolling);
 * this.startService(pollingIntent);
 * 
 * For now polling simply entails telling the ConversationManager to sync to the
 * server.
 */
public class LyricooPollingService extends IntentService {
	private static Handler mHandler;
	private static Runnable mPoller;
	private static boolean mIsPolling;

	// how often we should poll, in milliseconds
	// every ten seconds for now
	private static final int POLLING_INTERVAL = 10 * 1000;

	public LyricooPollingService() {
		super("Lyricoo Polling");

		// initialize static variables the first time this is called
		if (mHandler == null) {
			mIsPolling = false;

			// Use a handler to schedule method calls at intervals
			mHandler = new Handler();

			// Create a runnable object that calls the poll method and then
			// schedules itself to run again
			mPoller = new Runnable() {
				@Override
				public void run() {
					if (mIsPolling) {
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

		if (doPolling) {
			startPolling();
		} else {
			stopPolling();
		}
	}

	private void startPolling() {
		if (!mIsPolling) {
			mIsPolling = true;
			mHandler.post(mPoller);
		}
	}

	private void stopPolling() {
		mIsPolling = false;

		// if we pass null, all callbacks are removed
		mHandler.removeCallbacksAndMessages(null);
	}

	/**
	 * Poll the server for updates
	 */
	private void poll() {
		// Just tell the conversation manager to do a sync
		// TODO: More sophisticated/efficient polling
		ConversationManager conversationManager = Session
				.getConversationManager();
		if (conversationManager != null) {
			conversationManager.sync(null);
		}
	}

}
