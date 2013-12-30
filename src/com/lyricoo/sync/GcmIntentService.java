package com.lyricoo.sync;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.lyricoo.Utility;
import com.lyricoo.messages.Message;
import com.lyricoo.messages.MessageException;
import com.lyricoo.session.Session;

public class GcmIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you
		// received in your BroadcastReceiver
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any messages that we're not interested in, or that we
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				// error
				Utility.log("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				// error
				Utility.log("Deleted messages on server: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				// Regular message hooray!

				// only try to receive a message if the user is logged in
				if (Session.isLoggedIn()) {

					// Pass the received message to the ConversationManager
					String contact = null;
					try {
						if (extras.containsKey("contact")) {
							contact = extras.getString("contact");
						}
						Session.getConversationManager().receiveMessage(
								new Message(extras), contact);
					} catch (MessageException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch(NullPointerException e){
						// if conversation manager is null for some reason we're in trouble
					}

					// send a system notification if the user hasn't disabled
					// them in their preferences
					SharedPreferences sharedPref = PreferenceManager
							.getDefaultSharedPreferences(this);
					boolean showNotifications = sharedPref.getBoolean(
							"pref_notifications", false);
					if (showNotifications) {
						sendNotification();
					}

				}
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification() {
		try {
			int numUnread = Session.getConversationManager().getUnreadCount();
			LyricooNotificationManager.newMessageNotification(
					getApplicationContext(), numUnread);
		} catch (Exception e) {
			// If there is no conversation manager then no one is logged in and
			// we shouldn't show a notification
		}

	}
}
