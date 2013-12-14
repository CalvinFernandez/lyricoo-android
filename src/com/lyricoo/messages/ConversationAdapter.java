package com.lyricoo.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lyricoo.R;
import com.lyricoo.music.Song;
import com.lyricoo.ui.PlayButton;


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
		
		// set the time
		TextView time = (TextView) rowView.findViewById(R.id.time);
		time.setText(msg.getDisplayableTime());
		
		// set the attached song if there is one, otherwise hide the play button
		PlayButton playButton = (PlayButton) rowView.findViewById(R.id.play_button);
		Song song = msg.getSong();
		if(song != null){
			playButton.setSong(song);
		} else {
			playButton.setVisibility(View.INVISIBLE);
		}
		
		return rowView;
	}

}
