package com.lyricoo.messages;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
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
		//	The getMessageType() intent parameter must be the intent you
		//  received in your BroadcastReceiver
		String messageType = gcm.getMessageType(intent);
		
		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that GCM
			 * will be extended in the future with new message types, just ignore 
			 * any messages that we're not interested in, or that we don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				// error
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				// error
				 sendNotification("Deleted messages on server: " + 
						extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				// Regular message hooray!
				sendNotification("Received: " + extras.toString());
				String contact = null;
				try {
					if (extras.containsKey("contact")) {
						contact = extras.getString("contact");
					}
					Session.getConversationManager().receiveMessage(new Message(extras), contact);
				} catch (MessageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	private void sendNotification(String msg) {
		LyricooNotificationManager.newMessageNotification(getApplicationContext(), 1);
	}
}
