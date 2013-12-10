package com.lyricoo.messages;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyricoo.R;
import com.lyricoo.Utility;
import com.lyricoo.music.Song;

/**
 * This class is used to create a listview of the user's messages based on each
 * of their conversations
 * 
 */
public class InboxAdapter extends BaseAdapter {

	private ArrayList<Conversation> mConversations;
	private Context mContext;

	public InboxAdapter(Context context, ArrayList<Conversation> messages) {
		mConversations = messages;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mConversations.size();
	}

	@Override
	public Object getItem(int position) {
		return mConversations.get(position);
	}

	@Override
	public long getItemId(int position) {
		// shouldn't apply to this situation since we don't have item ids
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// inflate the view if it is new, otherwise reuse it
		View rowView;
		if (convertView == null) {
			rowView = inflater.inflate(R.layout.inbox_list_item, parent, false);
		} else {
			rowView = convertView;
		}

		TextView contactName = (TextView) rowView
				.findViewById(R.id.message_contact_name);
		TextView content = (TextView) rowView
				.findViewById(R.id.message_content_preview);

		// get the conversation at this position
		Conversation convo = mConversations.get(position);

		// if there are unread messages show a notification indicating the
		// number unread. Otherwise hide it
		RelativeLayout unreadNotification = (RelativeLayout) rowView
				.findViewById(R.id.unread_message_counter);
		if (convo.hasUnread()) {
			unreadNotification.setVisibility(View.VISIBLE);
			TextView unreadCount = (TextView) rowView
					.findViewById(R.id.unread_count);
			unreadCount.setText(Integer.toString(convo.getUnreadCount()));
		} else {
			unreadNotification.setVisibility(View.GONE);
		}

		// set the message content
		Message msg = convo.getMostRecentMessage();
		content.setText(msg.getContent());

		String contact = convo.getContact().getUsername();

		// set the contact name
		contactName.setText(contact);

		// if there is a song, show the play button, otherwise show the
		// contact's initial
		Song song = msg.getSong();
		ImageView playButton = (ImageView) rowView
				.findViewById(R.id.play_button);
		TextView contactInitial = (TextView) rowView
				.findViewById(R.id.username_letter);
		if (song != null) {
			contactInitial.setVisibility(View.GONE);
			playButton.setVisibility(View.VISIBLE);

			// Place the song data in the view tag so it can be accessed on
			// click
			playButton.setTag(song);
		} else {
			contactInitial.setVisibility(View.VISIBLE);
			playButton.setVisibility(View.GONE);
			// set the first letter of the contact to be the icon
			contactInitial.setText(contact.substring(0, 1).toUpperCase());
			// set color
			int color = mContext.getResources().getColor(
					getLetterColor(contact));
			contactInitial.setTextColor(color);
		}

		return rowView;
	}

	/**
	 * Get a color for this message's contact initial based on a hash of the
	 * username
	 * 
	 * @param username
	 *            The username to get a color for
	 * @return A R.color resource id
	 */
	private int getLetterColor(String username) {
		int[] colors = new int[] { R.color.red, R.color.green, R.color.blue,
				R.color.Orange, R.color.purple, R.color.teal,
				R.color.DarkSalmon, R.color.DarkRed, R.color.DarkGoldenrod,
				R.color.MediumVioletRed, R.color.MediumOrchid,
				R.color.MediumPurple, R.color.Coral, R.color.Crimson,
				R.color.DeepPink, R.color.FireBrick, R.color.MediumSeaGreen,
				R.color.MediumSpringGreen, R.color.SeaGreen,
				R.color.GreenYellow, R.color.LimeGreen, R.color.IndianRed,
				R.color.Indigo, R.color.MediumAquamarine, R.color.MediumBlue,
				R.color.MediumSlateBlue, R.color.MidnightBlue,
				R.color.BlueViolet, R.color.DarkBlue, R.color.DarkSlateBlue,
				R.color.DarkTurquoise, R.color.OrangeRed, R.color.Orchid,
				R.color.DarkOliveGreen };

		// get an index into the colors array using the hash of the username.
		// Use bitwise AND to guarantee hashcode is positive
		int colorIndex = (username.hashCode() & 0x7FFFFFFF) % colors.length;
		return colors[colorIndex];

	}

}
