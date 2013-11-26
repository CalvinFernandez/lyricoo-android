package com.lyricoo;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.lyricoo.activity.MessagesActivity;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class GcmIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	
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
				try {
					
					Session.getConversationManager().receiveMessage(new Message(extras));
					
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
