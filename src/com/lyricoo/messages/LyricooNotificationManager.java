package com.lyricoo.messages;

import com.lyricoo.R;
import com.lyricoo.R.drawable;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * This class handles the delivery of system notifications to the user
 * 
 */
public class LyricooNotificationManager {
	/**
	 * Sends a notification that numMessages messages have arrived and are
	 * unread.
	 * 
	 * @param numMessages
	 */
	public static void newMessageNotification(Context context, int numMessages) {
		
		// always use the same id so old notifications can be updated instead of
		// creating duplicates
		int id = 1;

		// create the title and content of the notification
		String title, msg;
		if (numMessages > 1) {
			title = numMessages + " New Lyricoos";
			msg = "You have " + numMessages + " unread Lyricoos.";
		} else {
			title = "New Lyricoo";
			msg = "You have an unread Lyricoo.";
		}

		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		// TODO: Update with custom icon
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle(title);
		builder.setContentText(msg);
		builder.setNumber(numMessages);

		// Launch the messages activity when the notification is clicked
		Intent resultIntent = new Intent(context, InboxActivity.class);

		// Create a back stack for the activity
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(InboxActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		builder.setContentIntent(resultPendingIntent);
		
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// id allows you to update the notification later on.
		notificationManager.notify(id, builder.build());
	}
}
