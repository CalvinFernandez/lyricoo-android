package com.lyricoo.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lyricoo.Conversation;
import com.lyricoo.Message;
import com.lyricoo.R;
import com.lyricoo.Song;


public class ConversationAdapter extends ArrayAdapter<Message> {

	public ConversationAdapter(Context context, int resource, Conversation conversation) {
		super(context, resource, conversation.getMessages());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// get the message that corresponds to this position
		Message msg = getItem(position);

		// use a different view depending on whether the message was sent or
		// received
		View rowView;
		if (msg.isSent()) {
			rowView = inflater.inflate(R.layout.conversation_item_sent, parent,
					false);
		} else {
			rowView = inflater.inflate(R.layout.conversation_item_received, parent,
					false);
		}		

		// Set the message text
		TextView content = (TextView) rowView
				.findViewById(R.id.message_content);
		content.setText(msg.getContent());
		
		// set the attached lyricoo title if applicable
		Song song = msg.getSong();
		if(song != null){
			TextView lyricoo = (TextView) rowView
					.findViewById(R.id.attached_lyricoo_title);
			lyricoo.setText(song.getTitle());
		}
		
		return rowView;
	}

}
